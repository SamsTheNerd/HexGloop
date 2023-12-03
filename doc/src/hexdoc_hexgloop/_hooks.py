from importlib.resources import Package
from typing_extensions import override

from .book.page import pages
from .book import glooprecipe

from hexdoc.patchouli import BookContext

from hexdoc.plugin import (
    HookReturn,
    ModPlugin,
    ModPluginImpl,
    ModPluginWithBook,
    UpdateContextImpl,
    hookimpl,
)

import hexdoc_hexgloop

from .__gradle_version__ import FULL_VERSION, GRADLE_VERSION
from .__version__ import PY_VERSION


class HexgloopPlugin(ModPluginImpl, UpdateContextImpl):
    @staticmethod
    @hookimpl
    def hexdoc_mod_plugin(branch: str) -> ModPlugin:
        return HexgloopModPlugin(branch=branch)
    
    @staticmethod
    @hookimpl
    def hexdoc_load_tagged_unions() -> HookReturn[Package]:
        return [glooprecipe, pages]
    
    @staticmethod
    @hookimpl
    def hexdoc_update_context(context: BookContext) -> None:
        if context.props.modid != "hexgloop":
            return
        context.macros |= {
            # put your macros here
            "$(item)": "$(#b38ef3)"
        }


class HexgloopModPlugin(ModPluginWithBook):
    @property
    @override
    def modid(self) -> str:
        return "hexgloop"

    @property
    @override
    def full_version(self) -> str:
        return FULL_VERSION

    @property
    @override
    def mod_version(self) -> str:
        return GRADLE_VERSION

    @property
    @override
    def plugin_version(self) -> str:
        return PY_VERSION

    @override
    def resource_dirs(self) -> HookReturn[Package]:
        # lazy import because generated may not exist when this file is loaded
        # eg. when generating the contents of generated
        # so we only want to import it if we actually need it
        from ._export import generated

        return generated
    
    @override
    def jinja_template_root(self) -> tuple[Package, str]:
        return hexdoc_hexgloop, "_templates"
