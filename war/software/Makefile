
make_analyzer: rand_analyzer.ml rand_lex.mll rand_parse.mly
	ocamllex rand_lex.mll
	ocamlyacc rand_parse.mly
	ocamlc -c rand_parse.mli
	ocamlc -c rand_lex.ml
	ocamlc -c rand_parse.ml
	ocamlc -c rand_analyzer.ml
	ocamlc -o rand_analyzer.exe rand_lex.cmo rand_parse.cmo rand_analyzer.cmo	
	rm *.cmo *.cmi *.mli