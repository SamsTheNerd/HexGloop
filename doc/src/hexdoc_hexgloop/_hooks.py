from importlib.resources import Package

import hexdoc_hexgloop
from hexdoc.plugin import (HookReturn, LoadJinjaTemplatesImpl,
                           LoadResourceDirsImpl, LoadTaggedUnionsImpl,
                           ModVersionImpl, hookimpl)

from .__gradle_version__ import GRADLE_VERSION


class HexgloopPlugin(LoadResourceDirsImpl, ModVersionImpl, LoadTaggedUnionsImpl, LoadJinjaTemplatesImpl):
    @staticmethod
    @hookimpl
    def hexdoc_mod_version() -> str:
        return GRADLE_VERSION

    @staticmethod
    @hookimpl
    def hexdoc_load_resource_dirs() -> Package | list[Package]:
        # This needs to be a lazy import because they may not exist when this file is
        # first loaded, eg. when generating the contents of generated.
        from ._export import generated, resources

        return [generated, resources]
    
    @staticmethod
    @hookimpl
    def hexdoc_load_jinja_templates() -> HookReturn[tuple[Package, str]]:
        return hexdoc_hexgloop, "_templates"
