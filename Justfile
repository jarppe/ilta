set dotenv-load := true
project := "ilta"


help:
  @just --list


# Run CLJS tests
cljs-test:
  @npx shadow-cljs compile test


# Check for outdated deps
outdated:
  @clj -M:outdated


# Initialize dev setup:
init:
  npm i
  clojure -A:dev:test -P
  @echo "\n\nReady"


# Make icons:
make-icons:
  inkscape -w 512 -h 512  ./public/icon/ilta.svg  -o ./public/icon/ilta.512.png
  inkscape -w 256 -h 256  ./public/icon/ilta.svg  -o ./public/icon/ilta.256.png
  inkscape -w 128 -h 128  ./public/icon/ilta.svg  -o ./public/icon/ilta.128.png
  inkscape -w 64  -h 64   ./public/icon/ilta.svg  -o ./public/icon/ilta.64.png
  inkscape -w 32  -h 32   ./public/icon/ilta.svg  -o ./public/icon/ilta.32.png
