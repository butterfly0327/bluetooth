from dataclasses import dataclass
import time


@dataclass
class ControlConfig:
    servo_min_deg: float = 10.0
    servo_max_deg: float = 170.0
    servo_init_deg: float = 90.0
    control_hz: int = 125
    control_dt: float = 0.02
    rx_timeout: float = 0.30
    dead_band_x: float = 0.015
    step_target_low: float = 0.75
    step_target_mid: float = 0.875
    step_target_high: float = 1
    step_ramp_up: float = 0.0125
    step_ramp_down: float = 0.02
    xr_ema_alpha: float = 0.15
    v_ema_alpha: float = 0.15
    v_low_max: float = 0.35
    v_mid_max: float = 0.75
    gear_stable_ticks: int = 4
    dir_flip_err: float = 0.03
    rev_eps: float = 0.05


class FanController:
    def __init__(self, config: ControlConfig | None = None):
        self.config = config or ControlConfig()
        self.current_angle = self.config.servo_init_deg
        self.step_actual = 0.0
        self.gear_state = "LOW"
        self.gear_change_count = 0
        self.xr_filt = None
        self.v_filt = None
        self.dir_state = 0
        self.flip_pending = False
        self.flip_target_sign = 0

    def tick(self, xr_norm, v_norm, last_rx_time, set_servo):
        now = time.monotonic()
        if last_rx_time is None or (now - last_rx_time) > self.config.rx_timeout:
            print(f"[Tick] timeout: now={now:.3f} last_rx={last_rx_time}")
            self.step_actual = 0.0
            self.flip_pending = False
            set_servo(self.current_angle)
            return

        if xr_norm is None:
            print(f"[Tick] xr_norm None: v_norm={v_norm} last_rx={last_rx_time}")
            self.step_actual = 0.0
            self.flip_pending = False
            set_servo(self.current_angle)
            return

        if self.xr_filt is None:
            self.xr_filt = xr_norm
        else:
            self.xr_filt = self.xr_filt + self.config.xr_ema_alpha * (xr_norm - self.xr_filt)

        err = self.xr_filt - 0.5

        if abs(err) < self.config.dead_band_x:
            print(f"[Tick] deadband: err={err:.4f} angle={self.current_angle:.2f}")
            self.step_actual = 0.0
            self.flip_pending = False
            set_servo(self.current_angle)
            return

        desired_sign = 1 if err > 0 else -1

        if self.dir_state == 0:
            self.dir_state = desired_sign

        if desired_sign != self.dir_state:
            if abs(err) < self.config.dir_flip_err:
                desired_sign = self.dir_state
            else:
                self.flip_pending = True
                self.flip_target_sign = desired_sign

        step_target = None
        did_brake = False
        if self.flip_pending:
            step_target = 0.0
            self.step_actual = max(self.step_actual - self.config.step_ramp_down, 0.0)
            did_brake = True
            if self.step_actual <= self.config.rev_eps:
                self.dir_state = self.flip_target_sign
                self.flip_pending = False

        if not self.flip_pending:
            step_target = self._update_gear(v_norm)

        if not did_brake and step_target is not None:
            if self.step_actual < step_target:
                self.step_actual = min(self.step_actual + self.config.step_ramp_up, step_target)
            elif self.step_actual > step_target:
                self.step_actual = max(self.step_actual - self.config.step_ramp_down, step_target)

        new_angle = self.current_angle + self.dir_state * self.step_actual
        new_angle = self._clamp(new_angle, self.config.servo_min_deg, self.config.servo_max_deg)
        print(
            "[Tick] xr={:.4f} err={:.4f} dir={} step={:.3f} target={} angle={:.2f}->{:.2f}".format(
                self.xr_filt,
                err,
                self.dir_state,
                self.step_actual,
                step_target,
                self.current_angle,
                new_angle,
            )
        )

        if new_angle == self.current_angle:
            set_servo(self.current_angle)
            return

        self.current_angle = new_angle
        set_servo(self.current_angle)

    def _update_gear(self, v_norm):
        if v_norm is not None:
            if self.v_filt is None:
                self.v_filt = v_norm
            else:
                self.v_filt = self.v_filt + self.config.v_ema_alpha * (v_norm - self.v_filt)

        if self.v_filt is None:
            return self._gear_to_step_target()

        desired_gear = self._gear_for_speed(self.v_filt)

        if desired_gear != self.gear_state:
            self.gear_change_count += 1
        else:
            self.gear_change_count = 0

        if self.gear_change_count >= self.config.gear_stable_ticks:
            self.gear_state = desired_gear
            self.gear_change_count = 0

        return self._gear_to_step_target()

    def _gear_for_speed(self, v_filt):
        if v_filt <= self.config.v_low_max:
            return "LOW"
        if v_filt < self.config.v_mid_max:
            return "MID"
        return "HIGH"

    def _gear_to_step_target(self):
        if self.gear_state == "MID":
            return self.config.step_target_mid
        if self.gear_state == "HIGH":
            return self.config.step_target_high
        return self.config.step_target_low

    @staticmethod
    def _clamp(value, min_value, max_value):
        return max(min_value, min(max_value, value))
