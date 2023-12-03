# OptiFine3ify

An FML tweaker that makes OptiFine 1.7.10_HD_U_D6 compatible with Java 9 and higher. Does nothing with 1.7.10_HD_U_E7 which is already compatible.

**Note:** If you're using FastCraft and an lwjgl3ify version older than 1.5.7 alongside this, you need to disable `stbiTextureStiching` in lwjgl3ify's config to avoid a flashing screen issue.

## Why

OptiFine D6 [has been found to perform better than E7](https://gist.github.com/makamys/7cb74cd71d93a4332d2891db2624e17c#1-fastcraft-and-optifine-peculiarities) in some setups. But it [doesn't work with modern Java](https://github.com/GTNewHorizons/lwjgl3ify/issues/47). Or at least didn't until now.

## License

This project is licensed under the [Unlicense](UNLICENSE).
