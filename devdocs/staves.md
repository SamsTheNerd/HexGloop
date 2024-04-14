# Staves

HexGloop adds dyeable staves and a model predicate for when the staff is being used.

The dyeable staves work by using the tintindex, so the `layer1` texture of the staff model should be a grayscale texture of the part that should be dyed. The other layers of the texture should avoid overlapping with this grayscale area to avoid z-fighting.

The model predicate for when the staff is being used is `hexgloop:is_casting`, it's 0 when the staff is not in use and 1 when it is. For now this is just client side and based on when the player is using their own staff, but i do plan to one day add serverside support to see when others are using their staves.

Since addons/resource packs may want to support the dyeable staves when gloop is installed but also not have the raw grayscale texture exposed when it isn't installed, we can take advantage of how minecraft loads undefined predicates to switch to the dyeable item model only when gloop is installed:

### .../mymod/models/item/mystaff.json :
```json
{
    "parent": "minecraft:item/handheld_rod", // this is usually what staves use to be positioned correctly in the player's hand but isn't required
    "overrides": [
      {
        "model": "mymod:item/mystaff_dyeable", // a model file with dyeable texture on layer1
        "predicate": {
          "hexgloop:is_casting": 0.0 // this override will simply be skipped if gloop is not installed. you'll still need this even if you don't plan on adding a separate casting model/texture
        }
      },
      {
        "model": "mymod:item/mystaff_dyeable_casting", // a model file for a dyeable texture when the staff is in use - you don't need this section if you don't have/want a casting model
        "predicate": {
          "hexgloop:is_casting": 1
        }
      }
    ],
    "textures": {
        "layer0": "mymod:item/mystaff" // texture to use when gloop isn't installed
    }
}
```

### .../mymod/models/item/mystaff_dyeable.json :
```json
{
    "parent": "minecraft:item/handheld_rod",
    "textures": {
        "layer0": "mymod:item/mystaff_base", // base staff texture with the dyeable bit cut out
        "layer1": "mymod:item/mystaff_dyepart" // part of the texture to be dyed
    }
}
```

### .../mymod/models/item/mystaff_dyeable_casting.json :
```json
{
    "parent": "minecraft:item/handheld_rod",
    "textures": {
        "layer0": "mymod:item/mystaff_base_casting", // base staff texture with the dyeable bit cut out
        "layer1": "mymod:item/mystaff_dyepart_casting" // part of the texture to be dyed
    }
}
```