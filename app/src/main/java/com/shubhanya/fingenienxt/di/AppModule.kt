package com.shubhanya.fingenienxt.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // You can provide other app-wide dependencies here if needed
    // For example, SharedPreferences:
    // @Provides
    // @Singleton
    // fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
    //     return context.getSharedPreferences("fingenie_prefs", Context.MODE_PRIVATE)
    // }
}


/**
 * **Explanation:**
 * * **`@Module`**: Marks a class as a Hilt module, which provides instances of dependencies.
 * * **`@InstallIn(SingletonComponent::class)`**: Specifies that the bindings in this module are available at the application level and will have a singleton scope (only one instance).
 * * **`@Provides`**: Marks a function within a module that provides an instance of a dependency.
 * * **`@Singleton`**: Ensures that Hilt provides the same instance every time it's requested.
 * * `FirebaseModule` provides instances of `FirebaseAuth` and `FirebaseFirestor
 */