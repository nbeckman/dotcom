(* rand.mll *)
(* This is the definition of the file that is the input to ocamllex *)
(* It is used to create a lexer. *)
(* This file is full of REGULAR_EXPRESSIONS that are needed to 
   create TOKENS *)
(* This file is an attempt to figure out how to use ML while at the same
   time actually solving this problem that Rand had earlier. The idea
   is to figure out when an expression has a string operator in it. 
   This of course will be no help to Rand because he needs one in C++, but
   all of the concepts will be the same. *)

{
  open Rand_parse
  exception Eof
}

rule token = parse
    [' ' '\t']    { token lexbuf }
  | ['\n']        { EOL }
  | '$'['A'-'Z' 'a'-'z']['A'-'Z' 'a'-'z' '0'-'9']   { ID }

  | ['0'-'9']+'.'['0'-'9']+                         { FLOAT }

  | ['0'-'9']+     { INT }
  | '"' [^ '"' '\\' '\n' '\r']* '"' { STRING }
  | '%'            { MOD }
  | '.'            { DOT }
  | '+'            { PLUS }
  | '-'            { MINUS }
  | '*'            { TIMES }
  | '/'            { DIV }
  | '('            { LPAREN }
  | ')'            { RPAREN }
  | _              { token lexbuf } (* Normally . matches any char... *)
  | eof            { raise Eof }
