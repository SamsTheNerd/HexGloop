# Changelog

## 0.2.0

### Added

- 

## 0.1.1

### Added

- tooltips showing how much dust raw media sources are worth (can search "worth dust" in REI/probably EMI to find all media sources)

### Fixed

- Torty's Max Purification not taking max stack size into account
- Torty transfer accepting negative counts
- Clientside crashing from pedestal not having a persistent uuid
- Inventorty item entity reach range far too small, bumped radius from 1 block to 4 block
- Inventorty -2 cursor stack when in hand wasn't working.
- Copying patterns from book to scroll not working on server
- Open hex notebook while casting keybind not showing up in keybind menu
- Mirror tooltips crashing on forge
- covered spellbook wheel would bring up wrong screen when closed
- place block not respecting mirror 
- patterns repeating on servers
- modded pipes/create not working with pedestal inventory on forge

### Changed

- Casting gloopifact patterns from other sources now gives a better mishap

## 0.1.0

### Added

- Gloopifact (new casting item with some iota manipulation abilities)
- Hex snacks & Hexxy-Os - conjure them with media and get a tasty little treat.
- Caster's Coins - a coin that can hold iotas that can only be read once
- Synchronous gloop
- Truename protection spell - void (almost) all references to your truename
- Hand Mirror - redirects spells that use your offhand to use an item entity stored by the mirror. Using the mirror also uses the stored item as if it were in your hand.
- Pedestals - gives a functional item entity based on the item stack in its inventory
- Slate chests
- Redstone related patterns
- Dimension related patterns
- Pattern to check if a position/entity is within ambit
- Nop pattern
- Catch pattern
- Keybind to open hex notebook from casting grid [client side]
- Right click hex notebook on placed scroll to copy the pattern from the current page onto the scroll.
- Gloop can now be used as a raw media source worth 2x charged amethyst
- Hexxy
- Sentinel Bed - always have ambit on this block
- Inventorty - a helpful companion for inventory management
- Accelerator - make circles go faster !!
- Locus system ( with hooks open for other addons if they'd like to add their own )
- Hexxed Glass - block raycasts pass through it
- Ritualist's Inscriber - takes a list and places slates with the next pattern on the list
- Dyeable Spellbook Covers
- More staffs

### Changed

- Multi-Focus and Fidgets will now loop around when shift scrolling or using the keybinds
- Sped up tooltip delay on iota wheels
- Casting Potion casts with its brewed pigment

### Fixed

- Casting ring letting non-staff casting draw media from your inventory
- Gloop dye uncraftable on forge
- Text Patterns easily overridden by other mods (like ftbranks)
- Gloop dye doesn't dupe stackable items anymore

## 0.0.4

### Added

- Chinese localization (thx ChuijkYahus !)
- Glooptastic advancement
- Hexal/MoreIotas label types
- Spectrum potion workshop casting potion recipes (thx Shibva !)

### Fixed

- Casting ring not opening when you clear it
- Casting ring spamming close sound if you hold shift + G
- Gloop Dye crafting unusable (should just need to hold it to fix it)
- Patterns showing as '!' in logs and reveals
- No way to brew soulglimmer casting potion (it will now take on the soulglimmer of the first player who holds it after brewing)
- Forge server issues
- Forge keybind issues (again)
- Brewing dust with glass bottle causing crash.

### Changed

- Made energizer harder to break
- Made energizer cheaper (now requires netherite scrap instead of ingots)
- Changed MultiFocus to use charged amethyst instead of foci in its recipe.
- Casting potions now stackable up to 16

## 0.0.3

### Fixed

- Fabric keybinds not working
- Changed double to number
- Fabric actually has wnboi as dependency

## 0.0.2

### Added

- Click to copy list

### Fixed

- Not launching on server
- Forge not having keybinds
- Spellbook not being label-able
- Trying to label unlabel-able item failing quietly (now throws a mishap)
- Casting Potion crash with hexxycraft hexcasting build
- Rival's gambit showing as lover's gambit in book

### Changes

- Moved craft potion into spells section of hex notebook

<br>

## Start of changelog - v0.0.1