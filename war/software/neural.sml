(* My first, poor atempt to make a neural network. *)
(* More or less copied from 
http://diwww.epfl.ch/mantra/tutorial/english/aneuron/html/index.html *)

(* A two-input neuron. *)
(* Two weights, threshold, transfer function *)
abstype Neuron = neuron' of (real* real * real * (real -> real))
with
  fun neuron (w1, w2, threshold, trans_fun) = 
	neuron' (w1, w2, threshold, trans_fun);

  fun fire (i1, i2, neuron'(w1, w2, threshold, trans_fun)) =
	trans_fun ((i1*w1 + i2*w2) - threshold) 
	
end;

(* Two potential transfer functions. *)
fun unit_step x = if 0.0 > x then 0.0 else 1.0;

fun sigmoid x = 1.0 / (1.0 + Math.pow(Math.e, 0.0 - x));


(* Sample Neurons *)
val AND = neuron(1.0, 1.0, 2.0, unit_step);
val OR  = neuron(1.0, 1.0, 1.0, unit_step);
val NOT = neuron(0.0, ~1.0, 0.0, unit_step);

(* Runs of those three neurons (Expected result)*)
val ex_1 = fire (1.0, 1.0, AND);  (* 1.0 *)
val ex_2 = fire (0.0, 1.0, AND);  (* 0.0 *)
val ex_3 = fire (0.0, 0.0, AND);  (* 0.0 *)
val ex_4 = fire (0.0, 1.0, OR);   (* 1.0 *)
val ex_5 = fire (69.0, 0.0, NOT); (* 1.0 *)