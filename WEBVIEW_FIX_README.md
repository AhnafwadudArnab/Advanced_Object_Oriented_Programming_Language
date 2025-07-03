# WebView Access Issue Fix

## Problem Description

You're encountering a JavaFX WebView access error:
```
java.lang.IllegalAccessError: superclass access check failed: class com.sun.javafx.sg.prism.web.NGWebView (in unnamed module @0x1090d13e) cannot access class com.sun.javafx.sg.prism.NGGroup (in module javafx.graphics) because module javafx.graphics does not export com.sun.javafx.sg.prism to unnamed module @0x1090d13e
```

This error occurs because:
1. Your system has JavaFX 21 installed (as shown in the stack trace)
2. JavaFX 21 has stricter module system restrictions
3. The WebView component is trying to access internal JavaFX graphics classes that are no longer accessible

## Solutions

### Option 1: Use Java 17+ with JavaFX 21 (Recommended)

This is the modern approach that properly handles the module system:

1. **Install Java 17 or higher** (if not already installed)
   - Download from: https://adoptium.net/
   - Add Java to your PATH

2. **Install Maven** (if not already installed)
   - Download from: https://maven.apache.org/download.cgi
   - Add Maven to your PATH

3. **Run the updated build script:**
   ```bash
   build.bat
   ```

4. **Run the application:**
   ```bash
   run.bat
   ```

The updated `pom.xml` includes:
- JavaFX 21 dependencies
- Proper module configurations
- `--add-opens` flags to allow WebView access

### Option 2: Use Simple Mode (Quick Fix)

If you don't have Maven or Java 17+:

1. **Use the simple run script:**
   ```bash
   run_simple.bat
   ```

This will use existing compiled classes and the map will fallback to a text-based view if WebView fails.

### Option 3: Manual Java 8 Setup

If you prefer to stick with Java 8:

1. **Ensure you have Java 8 with JavaFX 8:**
   - Download Java 8 from Oracle's archive
   - Make sure `jfxrt.jar` is in the JRE's `lib/ext` directory

2. **Use the original build and run scripts:**
   ```bash
   build.bat
   run.bat
   ```

## What Changed

### Files Modified:
- `pom.xml` - Updated to JavaFX 21 with proper module configuration
- `build.bat` - Now uses Maven for building
- `run.bat` - Added module system flags for JavaFX 21
- `MapViewApp.java` - Added fallback text-based view when WebView fails
- `run_simple.bat` - Updated for better compatibility

### MapViewApp Improvements:
- **Graceful fallback**: If WebView fails, shows a text-based location view
- **Better error handling**: Catches WebView creation errors
- **User-friendly**: Shows clear status indicators (🔴 Missing, 🟢 Found, ⚫ Deceased)

## Testing the Fix

1. **Try Option 1 first** (Java 17+ with JavaFX 21)
2. **If that doesn't work**, try Option 2 (Simple mode)
3. **The map functionality will either show:**
   - Interactive web-based map (if WebView works)
   - Text-based location list (if WebView fails)

## Troubleshooting

### If you get "Maven not found":
- Download Maven from: https://maven.apache.org/download.cgi
- Add Maven to your PATH

### If you get "Java 17+ not found":
- Download Java 17+ from: https://adoptium.net/
- Add Java to your PATH

### If the map still doesn't work:
- The text-based fallback will automatically activate
- You'll see a list of missing persons with their locations

### If you get compilation errors:
- Make sure you have the latest version of the code
- Try running `mvn clean compile` manually
- Check that all dependencies are properly downloaded

## Benefits of the Fix

1. **Robust**: Works with multiple Java versions
2. **User-friendly**: Clear error messages and fallbacks
3. **Future-proof**: Uses modern JavaFX module system
4. **Backward compatible**: Still works with Java 8 if needed

## Module System Flags

The fix includes these important JVM flags:
```
--add-opens javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED
--add-opens javafx.graphics/com.sun.javafx.sg.prism.web=ALL-UNNAMED
--add-opens javafx.web/com.sun.webkit=ALL-UNNAMED
--add-opens javafx.web/com.sun.webkit.dom=ALL-UNNAMED
```

These flags allow WebView to access the necessary internal JavaFX classes.

The application will now handle the WebView issue gracefully and provide a functional map view regardless of the Java/JavaFX version you're using. 