import 'dart:convert';

import 'package:flutter/material.dart';

/// The class for package information.
/// Contains package name (e.g., com.facebook.katana), app name (e.g., Facebook), and app icon
class PackageInfo {
  PackageInfo({
    this.packageName,
    this.appName,
    this.appIconByteArray,
    this.path,
    this.size
  });

  /// Construct class from the json map
  factory PackageInfo.fromMap(Map map) => map == null
      ? null
      : PackageInfo(
          packageName: map['packageName'],
          appIconByteArray: _eliminateNewLine(map['appIcon']),
          appName: map['appName'],
          path: map['path'],
          size: map['size']
        );

  final String packageName;
  final String appName;
  final String appIconByteArray;
  final String path;
  final int size;

  /// Get flutter's `Image` widget from the byte array of app icon
  Image getAppIcon({
    BoxFit fit = BoxFit.fill,
    double height = 32.0,
    double width = 32.0,
  }) =>
      appIconByteArray != null
          ? Image.memory(
              base64Decode(appIconByteArray),
              fit: fit,
              height: height,
              width: width,
            )
          : null;

  @override
  String toString() =>
      'Package: $packageName, AppName: $appName, IconByteArray size: ${appIconByteArray?.length}';
}

String _eliminateNewLine(String s) => s?.replaceAll('\n', '');
