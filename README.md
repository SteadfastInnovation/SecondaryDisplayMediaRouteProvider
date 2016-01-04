Secondary Display Media Route Provider
======

A Media Route Provider for [Secondary Displays][1] to use with the [Support Media Router API][2].

Easily search for and connect with the Media Router API by adding the library's custom category.
 
Example:

    mMediaRouteSelector = new MediaRouteSelector.Builder()
                    .addControlCategory(SecondaryDisplayMediaRouteProvider.CATEGORY_SECONDARY_DISPLAY_ROUTE)
                    .build();
                    
See sample project for a more complete example.

Warning
--------

By default the this Provider runs in a separate process *your.package.name:mrp*, which causes your
**Application class to be loaded again outside of your main process**. This maybe causing unnecessary
computational overhead or loading resources that are never used.

Download
--------

[ ![Download](https://api.bintray.com/packages/steadfastinnovation/android/SecondaryDisplayMediaRouteProvider/images/download.svg) ](https://bintray.com/steadfastinnovation/android/SecondaryDisplayMediaRouteProvider/_latestVersion)

Gradle dependency:

    dependencies {
        compile 'com.steadfastinnovation.mediarouter:secondary-display-provider:1.0.0'
    }


License
-------

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


 [1]: http://developer.android.com/about/versions/android-4.2.html#SecondaryDisplays
 [2]: http://developer.android.com/guide/topics/media/mediarouter.html
