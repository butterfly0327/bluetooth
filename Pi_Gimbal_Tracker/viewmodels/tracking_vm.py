from core.observable import Observable
import config

class TrackingViewModel:
    def __init__(self):
        self.pan_angle = Observable(90)
        self.tilt_angle = Observable(90)
    
    def update(self, x, y):
        if x is None or y is None: return
        
        self.pan_angle.value = new_pan
        self.tilt_angle.value = new_tilt