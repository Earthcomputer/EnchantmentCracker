# Enchantment Cracker
Cracking the XP seed in Minecraft and choosing your enchantments.

## Tutorials
- [Original video tutorial](https://youtu.be/hfiTZF0hlzw)
- [Updated picture tutorial](https://imgur.com/a/oaxCC5x)
- **The UI is subject to change and may have updated since the tutorials**

## Installation Instructions
Download the latest zip file from [https://github.com/Earthcomputer/EnchantmentCracker/releases]
and extract into a place you won't lose it, such as your Desktop.

If you're a Windows user, you should double-click the batch file in the `bin` folder. Batch files
have a cog icon next to their name.

If you're not using Windows, you should instead run the other file in the `bin` folder (called `enchcracker`,
not `enchcracker.bat`). How to "run" a shell script like this may differ from system to system and I
can't give instructions for every system.

You must have Java installed to run the enchantment cracker. You may install Java
from [here](https://www.oracle.com/technetwork/java/javase/downloads/index.html).

## Reporting Bugs
Feel free to report bugs and ask any questions you may have at
[https://github.com/Earthcomputer/EnchantmentCracker/issues], but
make sure to *search* whether anyone has reported your bug or asked
your question before.

## Building from Source
Assuming you have `git` installed, run the following commands from the
Command Prompt/Terminal.
```
git clone https://github.com/Earthcomputer/EnchantmentCracker
cd EnchantmentCracker
gradlew build
```
On Linux/MacOS, run `./gradlew build` instead of `gradlew build`.

The resulting zip file may be found in the `build/distributions` folder.

Alternatively you can run directly from source without building, using
the `gradlew run` command.

## Contributing
All (sensible) contributions are welcome!

If you use Eclipse, run `gradlew eclipse` to generate an Eclipse project,
then use `File > Import > General > Existing Projects into Workspace` inside
Eclipse. Navigate to the EnchantmentCracker folder and click import.

If you use IntelliJ IDEA, it can import the Gradle project directly.
