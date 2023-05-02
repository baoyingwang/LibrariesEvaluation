python Test.py

#e.g.
#$ python Test.py
#{1,{2,3},4}
#(init { (value 1) , (value (init { (value 2) , (value 3) })) , (value 4) })

python Translate.py
#e.g.
#$ python Translate.py 
#{1,2}
#"
#\u0001
#\u0002
#"