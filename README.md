# ![alt text](https://github.com/shahimclt/pixman/blob/master/app/src/main/res/mipmap-hdpi/ic_launcher_round.png "Pixman Logo") Pixman

PixMan is a powerful image editing app for Android which helps in applying a few image operations on a selected image.

## Features

* Import images from your gallery
* Flip images vertically or horizontally
* Reduce opacity by 50%
* Add a Greedygame text and Logo to the image
* 3 steps Undo and Redo
* Toggle between original and edited image
* Save edited image to gallery

... and ...

* Dark mode support for Android 10
* Adaptive icons support

## Some random thoughts

- This project was coded in Kotlin, which I'm only recently learning. 🤞
- Opted to go with the _Single Activity|Multiple fragments_ approach.
- The undo and redo features are very primitive and not very memory efficient : sufficient for the task at hand but could be improved
- I have an Android 10 device, so thats what I primarily tested on. I'm pretty sure its fine but more testing is required on other android versions. `#TODO` when I get the time.
- The editor page (fragment) could also do with some refactoring - if you need to add more features, that is.
- Any resemblance of the UI to a popular image editing app is purely coincidental ;-).
- The save feature is locking up the main thread. Need to use coroutines.
- Images dont keep their orientation when imported through `BitmapFactory`. Had to jump through some EXIF hoops to get them working.
