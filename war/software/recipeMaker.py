# Recipe Maker
# v1.0
#
# Nels Beckman
#
# April 11, 2006
#
# This is a simple GUI application for creating recipes in the RecipeML.
# format. After going through the steps of adding ingredients and steps
# of the directions, if you press output to file, a file browser will
# open up that you can use to specify where the output xml file should
# go.
#
#
#
# Walkthrough of a simple use case:
# 1. Start by filling in the title of the recipe into the Title text entry.
# 2. The name of the first ingredient goes into the Ing text entry block.
# 3. For QTY and UNIT, you might enter 1 and Cup (If there is no unit,
#    eg. 1 egg, just leave UNIT empty).
# 4. Hit the 'Add Ingredient' button.
# 5. Repeat this process until all ingredients have been addded.
# 6. For each step of the directions, type that step into the Direction text
#    entry field and then hit 'Add Direction.'
# 7. Hit 'Print to File' and navigate to the directory where the file should
#    be saved, type in the name, and hit 'Save.'
#

from Tkinter import *
import tkFileDialog

class Ingredient:
    def __init__(self, name, qty, unit):
        self.name = name
        self.qty = qty
        
        if unit is "":
            self.unit = None
        else:
            self.unit = unit

    def toString(self):
        to_return =  "<ing><item>"+self.name+"</item><amt><qty>"
        to_return += self.qty+"</qty>"

        if self.unit is not None:
            to_return +="<unit>"+self.unit+"</unit>"

        to_return += "</amt></ing>\n"

        return to_return

class Direction:
    def __init__(self, step):
        self.step = step

    def toString(self):
        to_return = "<step>"+self.step+"</step"
        return to_return

class Recipe:
    def __init__(self):
        self.directions = []
        self.ingredients = []
        self.title = ""
        self.styleSheet = ""
    
    def setTitle(self, title):
        self.title = title

    def setStyleSheet(self, uri):
        self.styleSheet = uri

    def addDirection(self, direction):
        self.directions.append(direction)

    def addIngredient(self, ingredient):
        self.ingredients.append(ingredient)

    def toString(self):
        # UGLY TEXT IN CODE, HERE I COME!!
        to_return = '<?xml version="1.0" encoding="UTF-8"?>\n'
        to_return += '<!DOCTYPE recipeml PUBLIC "-//FormatData//DTD RecipeML 0.5//EN"\n'
        to_return += '"http://www.formatdata.com/recipeml/recipeml.dtd">\n'

        if self.styleSheet is not "":
            to_return += '<?xml-stylesheet href="'+self.styleSheet+'" type="text/xsl"?>\n'

        to_return += '<recipeml version="0.5">\n'
        to_return += ' <recipe><head><title>'+self.title+'</title></head>\n'
        to_return += '  <ingredients>\n'

        #print ingredients
        for ing in self.ingredients:
            to_return += '   '+ing.toString()+'\n'

        to_return += '  </ingredients>\n'
        to_return += '  <directions>\n'

        #print steps
        for step in self.directions:
            to_return += '   '+step.toString()+'\n'

        to_return += '  </directions>\n'
        to_return += ' </recipe>\n'
        to_return += '</recipeml>\n'

        return to_return
        
class NelsGUI:
    def __init__(self, master):
        self.myRecipe = Recipe()
        
        frame = Frame(master)
        frame.pack()

        # FUGLY GUI SETUP!! YAY!
        self.mainPanel = Frame(frame, bd=2, relief=SUNKEN)
        self.mainPanel.pack()

        self.subPanel1 = Frame(self.mainPanel)
        self.subPanel1.pack()

        self.titleLabel = Label(self.subPanel1, text="Title:")
        self.titleLabel.pack(side=LEFT)

        self.titleEntry = Entry(self.subPanel1)
        self.titleEntry.pack(side=LEFT)

        self.subPanel2 = Frame(self.mainPanel)
        self.subPanel2.pack()

        self.styleSheetLabel = Label(self.subPanel2, text="Stylesheet (Optional):")
        self.styleSheetLabel.pack(side=LEFT)

        self.styleSheetEntry = Entry(self.subPanel2, text="format.xsl")
        self.styleSheetEntry.pack(side=LEFT)

        self.ingPanel = Frame(frame, bd=2, relief=SUNKEN)
        self.ingPanel.pack()

        self.ingPanel1 = Frame(self.ingPanel)
        self.ingPanel1.pack()

        self.ingLabel = Label(self.ingPanel1, text="Ing:")
        self.ingLabel.pack(side=LEFT)

        self.ingEntry = Entry(self.ingPanel1)
        self.ingEntry.pack(side=LEFT)

        self.ingPanel2 = Frame(self.ingPanel)
        self.ingPanel2.pack()

        self.qtyLabel = Label(self.ingPanel2, text="QTY:")
        self.qtyLabel.pack(side=LEFT)

        self.qtyEntry = Entry(self.ingPanel2, width=2)
        self.qtyEntry.pack(side=LEFT)

        self.unitLabel = Label(self.ingPanel2, text="UNIT:")
        self.unitLabel.pack(side=LEFT)

        self.unitEntry = Entry(self.ingPanel2, width=2)
        self.unitEntry.pack(side=LEFT)

        self.ingButton = Button(self.ingPanel, text="Add Ingredient", command=self.addIng)
        self.ingButton.pack()

        self.dirPanel = Frame(frame, bd=2, relief=SUNKEN)
        self.dirPanel.pack()

        self.dirLabel = Label(self.dirPanel, text="Direction:")
        self.dirLabel.pack(side=LEFT)

        self.dirEntry = Entry(self.dirPanel)
        self.dirEntry.pack(side=LEFT)

        self.dirButton = Button(self.dirPanel, text="Add Direction", command=self.addStep)
        self.dirButton.pack()

        self.mainButton = Button(master, text="Print to File", command=self.printToFile)
        self.mainButton.pack()

    def printToFile(self):
        self.myRecipe.setTitle(self.titleEntry.get())
        self.myRecipe.setStyleSheet(self.styleSheetEntry.get())

        #now get filename
        filename = tkFileDialog.asksaveasfilename()

        if filename is not "":
            myFile = file(filename, mode='w+')
            myFile.write(self.myRecipe.toString())
            myFile.close()

            self.myRecipe = Recipe()
 
    def addIng(self):
        if self.ingEntry.get() is "":
            return
        
        ing = Ingredient(self.ingEntry.get(), self.qtyEntry.get(), self.unitEntry.get())
        self.myRecipe.addIngredient(ing)

        self.ingEntry.delete(0, END)
        self.qtyEntry.delete(0, END)
        self.unitEntry.delete(0, END)

    def addStep(self):
        if self.dirEntry.get() is "":
            return
        
        step = Direction(self.dirEntry.get())
        self.myRecipe.addDirection(step)

        self.dirEntry.delete(0, END)

root = Tk()
nels_gui = NelsGUI(root)
root.mainloop()
