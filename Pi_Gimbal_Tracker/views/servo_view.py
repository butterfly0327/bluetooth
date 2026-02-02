# views/servo_view.py
from adafruit_pca9685 import PCA9685
from adafruit_motor import servo
import board
import busio
import config

class ServoView:
    def __init__(self):
        i2c = busio.I2C(board.SCL, board.SDA)
        self.pca = PCA9685(i2c, address=0x60)
        self.pca.frequency = config.SERVO_FREQ
        
        self.pan = servo.Servo(self.pca.channels[config.PIN_PAN], min_pulse=500, max_pulse=2500)
        self.tilt = servo.Servo(self.pca.channels[config.PIN_TILT], min_pulse=500, max_pulse=2500)
        
        self.current_pan = 90.0
        self.current_tilt = 90.0
        self.update_servos()

    def update_position(self, target_x, target_y):
        raw_pan = (1.0 - target_x) * 180
        
        safe_pan = max(10, min(170, raw_pan))
        
        if abs(safe_pan - self.current_pan) < 1.0:
            return 

        self.current_pan = safe_pan
        self.update_servos()

    def set_pan_angle(self, angle):
        print(f"[Servo] set_pan_angle {angle:.2f}")
        self.current_pan = angle
        self.update_servos()
            
    def update_servos(self):
        self.pan.angle = self.current_pan
        self.tilt.angle = self.current_tilt

    def cleanup(self):
        self.pca.deinit()
