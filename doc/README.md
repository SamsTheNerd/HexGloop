# hexdoc-hexgloop

Python web book docgen and [hexdoc](https://pypi.org/project/hexdoc) plugin for HexGloop.

## Version scheme

We use [hatch-gradle-version](https://pypi.org/project/hatch-gradle-version) to generate the version number based on whichever mod version the docgen was built with.

The version is in this format: `mod-version.python-version.mod-pre.python-dev.python-post`

For example:
* Mod version: `0.11.1-7`
* Python package version: `1.0.dev0`
* Full version: `0.11.1.1.0rc7.dev0`

## Setup

```sh
python -m venv venv

.\venv\Scripts\activate  # Windows
source venv/bin/activate # anything other than Windows

# run from the repo root, not doc/
pip install -e .[dev]
```

## Usage

For local testing, create a file called `.env` following this template:
```sh
GITHUB_REPOSITORY=samsthenerd/Hexgloop
GITHUB_SHA=main
GITHUB_PAGES_URL=https://samsthenerd.github.io/Hexgloop/
```

Then run these commands to generate the book:
```sh
# run from the repo root, not doc/
hexdoc render doc/properties.toml _site/src/docs
hexdoc merge --src _site/src/docs --dst _site/dst/docs
```

Or, run this command to render the book and start a local web server:
```sh
hexdoc serve doc/properties.toml --src _site/src/docs --dst _site/dst/docs
```
