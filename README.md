# middor-Plus

[English](./README.md) | [简体中文](./README.zh-CN.md)

> A fork of [nktnet1/middor](https://github.com/nktnet1/middor) that adds a quick-access bubble,
> first-launch permission onboarding and a Simplified Chinese interface.

<div align="center">

[<img src="./misc/badges/github.png" alt="Get it on GitHub" width="260px" />](https://github.com/nktnet1/middor/releases)
[<img src="./misc/badges/obtainium.png" alt="Get it on Obtainium" width="260px" />](https://apps.obtainium.imranr.dev/redirect?r=obtainium://add/https://github.com/nktnet1/middor)
[<img src="./misc/badges/openapk.png" alt="Get it on OpenAPK" width="260px" />](https://www.openapk.net/middor/org.nktnet.middor/)
[<img src="./misc/badges/f-droid.svg" alt="Get it on F-Droid" width="260px" />](https://f-droid.org/packages/org.nktnet.middor)
[<img src="./misc/badges/izzy-on-droid.svg" alt="Get it on IzzyOnDroid" width="260px" />](https://apt.izzysoft.de/fdroid/index/apk/org.nktnet.middor)

</div>

middor-Plus is a free and open-source Android application for mirroring apps on your device.

With built-in support for horizontal flip and 180° rotation, it can be used to:

- Display apps on a HUD
- Project content onto a car windscreen (e.g. Google Maps navigation)
- View mirrored images and videos

It only supports devices running Android 14 QPR2 or higher, as it
relies on the single app screen sharing feature.

For more details, see
[Android 14 - App Screen Sharing](https://developer.android.com/about/versions/14/features/app-screen-sharing).

For usage instructions, see [Discussion #2](https://github.com/nktnet1/middor/discussions/2).

## Differences from upstream

This fork is based on [nktnet1/middor](https://github.com/nktnet1/middor). It keeps everything
from upstream and adds a quick-access bubble, first-launch permission onboarding, and Simplified
Chinese localisation.

| Feature                                                | Upstream Middor | middor-Plus       |
|:-------------------------------------------------------|:---------------:|:-----------------:|
| Single-app mirror overlay                              | ✓               | ✓                 |
| Horizontal flip / 180° rotation                        | ✓               | ✓                 |
| Remove system bars                                     | ✓               | ✓                 |
| Start delay (seconds)                                  | ✓               | ✓                 |
| Persistent draggable quick bubble                      | ✗               | ✓ (on by default) |
| Start mirroring without opening the app                | ✗               | ✓                 |
| Bubble auto-hides while mirroring, returns after stop  | ✗               | ✓                 |
| First-launch permission onboarding                     | ✗               | ✓                 |
| Simplified Chinese UI                                  | ✗               | ✓                 |
| Per-app language switch (Android 13+)                  | ✗               | ✓                 |

### Quick bubble

- A small draggable bubble stays on screen once the setting is enabled
- Tapping it goes straight to the system app chooser, skipping the main screen entirely
- While a mirror overlay is running the bubble hides itself, and returns once mirroring stops
- It needs the overlay permission, which the app requests on first launch
- Toggle it in <b>Settings</b> under <b>Quick Bubble</b>

Note that the bubble does not detect the foreground app for you — the system chooser still asks
which app to capture. It only removes the need to open the app first.

### Localisation

- Ships with English (default) and Simplified Chinese
- Both locales are declared in `res/xml/locales_config.xml`, so they appear in the system
  per-app language settings

## Permissions

- <b>SYSTEM_ALERT_WINDOW</b>: to draw both the mirror overlay and the quick bubble on top of other apps
- <b>FOREGROUND_SERVICE</b>: to run the mirror service continuously while the overlay is active
- <b>FOREGROUND_SERVICE_MEDIA_PROJECTION</b>: for capturing the screen content of the underlying app
- <b>FOREGROUND_SERVICE_SPECIAL_USE</b>: to keep the quick bubble service running in the background
- <b>POST_NOTIFICATIONS</b>: (optional) to display the mirror service notification with additional actions

## Contact

- support@middor.nktnet.org

## License

This project is licensed under the GNU Affero General Public License v3.0 or later.

See the [LICENSE](./LICENSE) file for details.

## Attribution

middor-Plus is a fork of [Middor](https://github.com/nktnet1/middor) by nktnet1, distributed
under the same GNU AGPL v3.0 or later.

## Screenshots

<div align="center">
  <img src="./metadata/en-US/images/phoneScreenshots/001.landing-screen.png" width="275px" alt="Phone Screenshot 1" />&nbsp;
  <img src="./metadata/en-US/images/phoneScreenshots/002.landing-screen-start.png" width="275px" alt="Phone Screenshot 2"/>&nbsp;
  <img src="./metadata/en-US/images/phoneScreenshots/003.landing-screen-app-selection.png" width="275px" alt="Phone Screenshot 3" />&nbsp;
  <img src="./metadata/en-US/images/phoneScreenshots/004.clock-app-flipped.png" width="275px" alt="Phone Screenshot 4" />&nbsp;
  <img src="./metadata/en-US/images/phoneScreenshots/005.settings-screen.png" width="275px" alt="Phone Screenshot 5" />&nbsp;
  <img src="./metadata/en-US/images/phoneScreenshots/006.help-screen.png" width="275px" alt="Phone Screenshot 6" />
</div>
