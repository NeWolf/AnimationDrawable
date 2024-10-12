# AnimationDrawable
[![](https://jitpack.io/v/NeWolf/AnimationDrawable.svg)](https://jitpack.io/#NeWolf/AnimationDrawable)
* Support APNG & Animated in Android
* Efficient decoder
* Support Drawable usage and glide library module
* Support animation play control
* Support still image
* Low memory usage

## Step 1. Add the JitPack repository to your build file

```
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```
## Step 2. Add the dependency
```
	dependencies {
	        implementation 'com.github.NeWolf.AnimationDrawable:APNG:V2.1.0'
	}
```

imageView.setImageDrable(APNGDrawable.fromResource(context,resId))
