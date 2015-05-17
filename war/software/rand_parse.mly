/* rand_parse.mly */
/* This file defines a GRAMAR the language that we are parsing. */
/* On the right in curly brakets are the actions to be taken upon
   finding the given rule on the left. If were were creating an
   abstract syntax tree, then right here on the right is where we
   would say things like 'create new addition node.' */
/* This dope file is the parser for a simple little program that checks to
   see if there are any string opeators in a given expression */
%token EOL INT MOD DOT PLUS MINUS FLOAT
%token TIMES DIV LPAREN RPAREN STRING
%token ID EOL
%left PLUS MINUS DOT
%left TIMES DIV MOD
%nonassoc UMINUS
%start main
%type <bool> main

%%
main:
    expr EOL                { $1    }

expr:
    INT                     { false }
  | FLOAT                   { false }
  | ID                      { false }
  | STRING                  { false }
  | LPAREN expr RPAREN      { $2    }
  | expr DOT expr           { true  }
  | expr PLUS expr          { $1 || $3 }
  | expr MINUS expr         { $1 || $3 }
  | expr TIMES expr         { $1 || $3 }
  | expr DIV   expr         { $1 || $3 }
  | MINUS expr %prec UMINUS { $2 }
;
