# FFChooser - File And Folder Chooser
[![](https://img.shields.io/badge/jitpack-v0.1-brightgreen.svg?style=for-the-badge)](https://jitpack.io/#Porush/FFChooser) 
[![GitHub issues](https://img.shields.io/github/issues/Porush/FFChooser.svg?style=for-the-badge)](https://github.com/Porush/FFChooser/issues)
 [![GitHub license](https://img.shields.io/github/license/Porush/FFChooser.svg?style=for-the-badge)](https://github.com/Porush/FFChooser/blob/master/LICENSE)

> **This android library (repository) is under development and not ready for use.**

# # Screenshots
![screenshots](https://raw.githubusercontent.com/Porush/FFChooser/master/screenshots/screenshots.png "screenshots")

<!---
# Video
[![Video 1](http://img.youtube.com/vi/DYfP-UIKxH0/0.jpg)](http://www.youtube.com/watch?v=DYfP-UIKxH0)
--->

# # Installation
Add this to your root build.gradle file under repositories:

```Gradle
allprojects {
	repositories {
		maven { url "https://jitpack.io" }
	}
}
```

Add this to your app level build.gradle as dependency:
```Gradle
compile 'com.github.Porush:FFChooser:v0.1'
```

# # Use
```Java
FFChooser ffChooser = new FFChooser(MainActivity.this, FFChooser.Select_Type_File);
ffChooser.setShowHidden(false);
ffChooser.setShowThumbnails(true);
ffChooser.setMultiSelect(false); // Files only.
ffChooser.setShowGoogleDrive(false); // If installed.
ffChooser.setShowOneDrive(false); // If installed.
ffChooser.setOnSelectListener(new FFChooser.OnSelectListener() {
        @Override
        public void onSelect(int type, String path) {
                switch (type) {
                        case FFChooser.Type_None:
                                // Canceled
                                break;
                        case FFChooser.Type_Local_Storage:
                                break;
                        case FFChooser.Type_Google_Drive_Storage:
                                break;
                        case FFChooser.Type_One_Drive_Storage:
                                break;
                }
        }
});
ffChooser.show();
```

# # License
```
MIT License

Copyright (c) 2018 Porush Manjhi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
