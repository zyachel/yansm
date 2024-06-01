# YANSM

Yet Another Network Speed Monitor

## About

A Quick Settings tile to see current internet speed.

## Why

Stock android doesn't have the ability to see network usage in real-time, but you may want to
monitor that for myriad of reasons like:

1. You're using cellular data and it isn't cheap.

2. You want to make sure [some app isn't sending gigabytes of data][tiktok background data usage] in
   the background without your knowledge.

3. You just want to have that monitoring power.

## Installation

Go to [GitHub releases page][github release url] and download the app-release.apk

## Usage

See [USAGE.md][usage] for instructions.

## To-Do

- [] fix speed going to 0kb/s every other second
- [] add ability to see speed via a persistent notification
- [] show speed in the icon itself
- [] add other fluff
- [] submit app to f-droid

## Credits

- much of the code related to getting speed is taken
  from [NetworkUsage by @JahidHasanCO][network usage] (MIT License).
- implementation of quick settings tile from [Android docs][android docs] (Apache 2.0 license).

## Contribution

Anything is welcomed.

## Copying

YANSM is free software: you can redistribute it and/or modify it under the terms of the GNU General
Public License as published by the Free Software Foundation, either version 3 of the License, or (at
your option) any later version.

YANSM is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
License for more details.

You should have received a copy of the GNU General Public License along with YANSM. If not,
see <https://www.gnu.org/licenses/>.

<!-- links -->

[tiktok background data usage]: https://old.reddit.com/r/mildlyinfuriating/comments/10j3xj6/why_did_tiktok_use_3gb_of_background_mobile_data/

[github release url]: https://github.com/zyachel/yansm/releases

[usage]: docs/USAGE.md

[network usage]: https://github.com/JahidHasanCO/NetworkUsage

[android docs]: https://developer.android.com/develop/ui/views/quicksettings-tiles
