(* rand_analyzer.ml *)
(* ML program for telling if a given expression has a 
   string op in it *)

let _ =
  try
    let lexbuf = Lexing.from_channel stdin in
    while true do
      let result = Rand_parse.main Rand_lex.token lexbuf in
        match result with
            true -> print_endline "Yes there was a string op.";
	      flush stdout
	  | false -> print_endline "No there was no string op.";
	      flush stdout
    done
  with Rand_lex.Eof ->
    exit 0
