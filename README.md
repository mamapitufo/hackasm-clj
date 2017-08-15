# hackasm-clj

This software assembles HACK assembly programs, as described in chapter 6 of the book
["The Elements of Computing Systems"](http://www.nand2tetris.org/book.php) by Noam Nisan
and Shimon Schocken.

## Usage

The program accepts a single argument, an assembly program for the HACK architecture with the `.asm`
extension. It will produce an assembled machine language program in a file with the same path as the
source but with a `.hack` extension. If the destination file exists it will be overwritten.

```sh
$ java -jar hackasm-clj-0.1.0-SNAPSHOT-standalone.jar [Program.asm]
```

## License

Copyright © 2017 Alberto Brealey-Guzman

Distributed under the GNU General Public License version 3.
