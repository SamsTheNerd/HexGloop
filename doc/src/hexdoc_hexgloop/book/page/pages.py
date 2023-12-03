from typing import Any, Self

from hexdoc_hexcasting.metadata import HexContext
from hexdoc_hexcasting.utils.pattern import PatternInfo, RawPatternInfo
from pydantic import ValidationInfo, field_validator, model_validator

from hexdoc.minecraft import LocalizedStr
from hexdoc.minecraft.recipe import CraftingRecipe
from hexdoc.patchouli import BookContext
from hexdoc.patchouli.page import PageWithText, PageWithTitle
from hexdoc.utils import cast_or_raise

from ..glooprecipe import ItemFlayRecipe, Gloopcipe


class ItemFlayPage(PageWithText, type="hexgloop:itemflay"):
    recipe: ItemFlayRecipe

class GloopcipePage(PageWithText, type="hexgloop:gloopcipe"):
    recipe: Gloopcipe