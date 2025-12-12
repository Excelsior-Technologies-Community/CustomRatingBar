# **EasyRatingBar**

A simple **Android library** to provide **custom rating bars** with Stars, Hearts, and Emojis.  
Users can **rate items** using **touch** or **swipe gestures**, with **animated fill** and **custom drawables**.

This project includes a library module (`easy_rating_bar`) and a sample app demonstrating its usage.

## ✨ **Features**

- **Multiple Rating Types:**
  - Stars  
  - Hearts  
  - Emojis  

- **Customizable Attributes**:  
  - Number of icons (`starCount`)  
  - Icon size (`iconSize`)  
  - Spacing between icons (`iconSpacing`)  
  - Filled color (`filledColor`)  
  - Empty color (`emptyColor`)  
  - Use custom drawables (`useDrawables`)  
  - Enable touch/swipe selection (`enableSwipe`)  
  - Enable animation (`enableAnimation`)  
  - Set as indicator only (`isIndicator`)

- Animated Fill – Smooth animation when rating changes
- Emoji Support – Use your own custom drawable emojis
- Touch & Swipe – Rate by tapping or swiping across icons
- Fallback Drawing – Automatically draws shapes if custom drawables are not provided

## ✨ **Preview**

<p align="center">
  <img src="https://github.com/user-attachments/assets/ab2821a4-b9fd-44bc-bd66-673b04724456"
       alt="Demo GIF"
       width="200">


</p>

## 📦 Installation

**Step 1:** Add JitPack repository to your root `build.gradle` (or `settings.gradle` for newer projects):

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
**Step 2:** Add the dependency to your app module build.gradle:
```
implementation 'com.github.Excelsior-Technologies-Community:EasyRatingBar:1.0.0'
```

**📌 Usage in XML**
```
<com.ext.easy_rating_bar.EasyRatingBar
    android:id="@+id/starRatingBar"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:ratingType="star"
    app:starCount="5
    app:rating="4.5"
    app:iconSize="50dp"
    app:iconSpacing="12dp"
    app:filledColor="@android:color/holo_orange_light"
    app:emptyColor="@android:color/darker_gray"
    app:isIndicator="false"
    app:enableAnimation="true"
    app:enableSwipe="true"
    app:useDrawables="true"/>

```

**Usage in Kotlin**
```
val starRatingBar = findViewById<EasyRatingBar>(R.id.starRatingBar)

// Listen for rating changes
starRatingBar.setOnRatingChangeListener { rating ->
    Toast.makeText(this, "Rating: $rating", Toast.LENGTH_SHORT).show()
}

// Programmatically set rating
starRatingBar.setRating(3.5f)

// Change colors dynamically
starRatingBar.setFilledColor(Color.YELLOW)
starRatingBar.setEmptyColor(Color.GRAY)
```

**Custom file**
```
<declare-styleable name="EasyRatingBar">
    <attr name="ratingType" format="enum">
        <enum name="star" value="0"/>
        <enum name="heart" value="1"/>
        <enum name="emoji" value="2"/>
    </attr>
    <attr name="starCount" format="integer"/>
    <attr name="rating" format="float"/>
    <attr name="iconSize" format="dimension"/>
    <attr name="iconSpacing" format="dimension"/>
    <attr name="filledColor" format="color"/>
    <attr name="emptyColor" format="color"/>
    <attr name="isIndicator" format="boolean"/>
    <attr name="enableAnimation" format="boolean"/>
    <attr name="enableSwipe" format="boolean"/>
    <attr name="useDrawables" format="boolean"/>
</declare-styleable>

```


**📄 License**
```
MIT License

Copyright (c) 2025 Excelsior Technologies

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

