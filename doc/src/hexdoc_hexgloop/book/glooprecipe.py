from typing import Any, Literal, List, Annotated, Union
from pydantic import Field 

from hexdoc.core import IsVersion, ResourceLocation
from hexdoc.minecraft.assets import ItemWithTexture
from hexdoc.minecraft.recipe import ItemIngredient, ItemIngredientList, Recipe
from hexdoc.model import HexdocModel, TypeTaggedUnion
from hexdoc.utils import NoValue

from hexdoc_hexcasting.book.recipes import BrainsweepRecipe, VillagerIngredient

class ItemFlayRecipe(Recipe, type="hexgloop:item_flaying"):
    villagerIn: VillagerIngredient
    ingredient: ItemIngredient
    inCount: int = 1
    result: ItemWithTexture
    resultCount: int = 1
    preserveNbt: bool = True
    addVillagerNbt: bool = False
    addedNbt: str = ""


class ItemIngredientWithCount(HexdocModel):
    ingredient: ItemIngredient
    count: int = 1

class Gloopcipe(Recipe, type="hexgloop:data_glooping"):
    result: ItemWithTexture
    priority: int = 0
    mediaCost: int = 10000
    ingredients: list[ItemIngredientWithCount | ItemIngredient]