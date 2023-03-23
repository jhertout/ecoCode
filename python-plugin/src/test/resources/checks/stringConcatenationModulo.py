'%s is smaller than %s' % ('one', 'two') # Noncompliant {{Using modulo concatenation to perform a string concatenation is not energy efficient.}}
"Hello, my name is %s." % "Graham"# Noncompliant {{Using modulo concatenation to perform a string concatenation is not energy efficient.}}
text = "Graham"
"Hello, my name is %s." % text# Noncompliant {{Using modulo concatenation to perform a string concatenation is not energy efficient.}}

