# ocaml_html_maker.py
# Nels Beckman
#
# March 27, 2006
#
# A simple script for turning Ocaml source code into pretty looking
# html files. Done as an excersize to further my 1337 python skillz.
# The hardest part about it all will be matching comments, since they
# can actually be nested in Ocaml.

import re
import sys
from sets import Set
from textwrap import dedent

class OcamlHTMLMaker:
    def __init__(self):
        # Constants; modifiable
        self.COMMENT_COLOR = "#FF0000"
        self.STRING_COLOR  = "#CCCC00"
        self.KEYWORD_COLOR = "#FF00FF"
        self.TEXT_COLOR    = "#FFFFFF"
        self.BG_COLOR      = "#000000"

        self.KEYWORD_SET   = Set(['and','as','assert','begin','class',
                                  'constraint','do','done','downto','else',
                                  'end','exception','external','false',
                                  'for','fun','function','functor','if',
                                  'in','include','inherit','initializer',
                                  'lazy','let','match','method','module',
                                  'mutable','new','object','of','open',
                                  'or','private','rec','sig','struct',
                                  'then','to','true','try','type','val',
                                  'virtual','when','while','with'])
        
    def translateKeywordsOnly(self, in_str, out_file_h):
        word_list = re.split("(\s)",in_str)

        for word in word_list:
            if word in self.KEYWORD_SET:
                out_file_h.write("<font color=\""+
                                 self.KEYWORD_COLOR+
                                 "\">"+word+"</font>")
            else:
                out_file_h.write(word)

        
    def translateFile(self, in_file, out_file):
        # Given two file handlers, open them both
        # translate and copy to the out_file

        try:
            in_file_h  = file(in_file,'r+')
            out_file_h = file(out_file,'w+')
        except IOError:
            print "Invalid file given: "+in_file+".\n"
            return
            

        #Boilerplate html stuff
        html_string = dedent('''\
        <html>
        <head>
        <style type="text/css">
        body {background-color:'''+self.BG_COLOR+'''}
        pre  {color:'''+self.TEXT_COLOR+'''}
        </style>
        <title>'''+in_file+'''</title>
        </head>
        <body>
        <pre>
        ''')

        out_file_h.write(html_string)
        
        comment_nest_depth = 0
        in_d_string = False

        
        for line in in_file_h:
            word_list = re.split("(\(\*|\*\)|\"|\'.\')",line)
            
            for word in word_list:
                if word == "(*":

                    if not in_d_string:
                        comment_nest_depth = comment_nest_depth + 1
                    
                        if comment_nest_depth == 1:
                            out_file_h.write("<font color=\""+
                                             self.COMMENT_COLOR+
                                             "\">"+word)
                        else:
                            out_file_h.write(word)
                            
                    else:
                        out_file_h.write(word)
                        
                elif word == "*)":
                    
                    if not in_d_string:
                        
                        comment_nest_depth = comment_nest_depth - 1
                        if comment_nest_depth == 0:
                            out_file_h.write(word+"</font>")
                        else:
                            out_file_h.write(word)

                    else:
                        out_file_h.write(word)
                        
                elif word == "\"":
                    if comment_nest_depth > 0:
                        out_file_h.write(word)
                    elif not in_d_string:
                        in_d_string = True
                        out_file_h.write("<font color=\""+
                                         self.STRING_COLOR+
                                         "\">"+word)
                    elif in_d_string:
                        in_d_string = False
                        out_file_h.write(word+"</font>")

                elif re.match("\'.\'", word):
                    if in_d_string or comment_nest_depth > 0:
                        out_file_h.write(word)
                    else:
                        out_file_h.write("<font color=\""+
                                         self.STRING_COLOR+
                                         "\">"+word+
                                         "</font>")
                        
                elif not in_d_string and comment_nest_depth == 0:
                    self.translateKeywordsOnly(word, out_file_h)
                else:
                    out_file_h.write(word)

        #closing HTML
        html_string = dedent('''\
        </pre>
        </body>
        </html>
        ''')

        out_file_h.write(html_string)

        #finish up
        out_file_h.close()
        in_file_h.close()
        


#MAIN
if len(sys.argv) != 3:
    print "Usage python ocaml_html_maker.py IN_FILE OUT_FILE\n"
else:
    x = OcamlHTMLMaker()
    x.translateFile(sys.argv[1], sys.argv[2])
#END MAIN

