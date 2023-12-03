from typing import Any, Literal

from hexdoc.core import IsVersion, ResourceLocation
from hexdoc.minecraft.assets import ItemWithTexture
from hexdoc.minecraft.recipe import ItemIngredient, ItemIngredientList, Recipe
from hexdoc.model import HexdocModel, TypeTaggedUnion
from hexdoc.utils import NoValue

from hexdoc_hexcasting.book.recipes import BrainsweepRecipe, VillagerIngredient_0_10

class ItemFlayRecipe(Recipe, type="hexgloop:item_flaying"):
    villagerIn: VillagerIngredient_0_10
    ingredient: ItemIngredient
    inCount: int = 1
    result: ItemWithTexture
    resultCount: int = 1
    preserveNbt: bool = True
    addVillagerNbt: bool = False
    addedNbt: str = ""


