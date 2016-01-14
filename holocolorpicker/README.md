#[**HoloColorPicker**](http://github.com/LarsWerkman/HoloColorPicker)
-----
by [*Lars Werkman*](http://github.com/LarsWerkman)

version 1.4

**local modification:**

[*ColorPicker.OnColorChangedListener*](src/main/java/com/larswerkman/holocolorpicker/ColorPicker.java#L259)

    public interface OnColorChangedListener {
    		public void onColorChanged(View view,int color);
    }
    ...
	onColorChangedListener.onColorChanged(this,color);