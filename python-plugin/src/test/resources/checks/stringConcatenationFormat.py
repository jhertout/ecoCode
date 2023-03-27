string_1 = "1"
string_2 = "2"
'{}{}'.format(string_1, string_2) # Noncompliant {{Using `str.format()` to perform a string concatenation is not energy efficient.}}

txt = "For only {price:.2f} dollars!"
print(txt.format(price = 49)) # Noncompliant {{Using `str.format()` to perform a string concatenation is not energy efficient.}}

txt1 = "My name is {fname}, I'm {age}".format(fname = "John", age = 36) # Noncompliant {{Using `str.format()` to perform a string concatenation is not energy efficient.}}
txt2 = "My name is {0}, I'm {1}".format("John",36) # Noncompliant {{Using `str.format()` to perform a string concatenation is not energy efficient.}}
txt3 = "My name is {}, I'm {}".format("John",36) # Noncompliant {{Using `str.format()` to perform a string concatenation is not energy efficient.}}

txt4=  string_1 + "My name is {}, I'm {}".format("John",36) # Noncompliant {{Using `str.format()` to perform a string concatenation is not energy efficient.}}
txt5 = "toto".format("John",36) # Noncompliant {{Using `str.format()` to perform a string concatenation is not energy efficient.}}

class TestObject():
    def format(self, my_string) -> str:
        return my_string
a = TestObject().format("test")