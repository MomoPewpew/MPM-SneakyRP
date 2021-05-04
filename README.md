# MPM-SneakyRP

This is an altered version of the 1.12.2 MorePlayerModels. It depends on SneakyEmotes and MultiCharacter.

This is not an official repository for MorePlayerModels.

List of changes:
* Added the halo part to the part menu
* Disabled analytics tracking
* /mpm entity was disabled due to some bad interactions. The MPM entity menu was turned into a GUI for the MultiCharacter /transform command.
* All body parts and MPM parts will now accept part offsets, for better interaction with SneakyEmotes.
* Created a getter for the PartConfigScale, for better interaction with SneakyEmotes
* /point will now account for your head pitch (and not just yaw)
* MPM camera has been changed to instead give you GTA style controls