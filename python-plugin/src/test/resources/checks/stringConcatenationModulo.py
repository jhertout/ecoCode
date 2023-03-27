'%s is smaller than %s' % ('one', 'two') # Noncompliant {{Using the modulo operator to perform a string concatenation is not energy efficient.}}
"Hello, my name is %s." % "Graham" # Noncompliant {{Using the modulo operator to perform a string concatenation is not energy efficient.}}
text = "Graham"
"Hello, my name is %s." % text # Noncompliant {{Using the modulo operator to perform a string concatenation is not energy efficient.}}
10 % 2

def return_string() -> str:
    return "test"
"my string %s" % return_string() # Noncompliant {{Using the modulo operator to perform a string concatenation is not energy efficient.}}


