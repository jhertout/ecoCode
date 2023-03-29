str = 'a'
str2 = str.join(["1", "2","3"])

str3 = str2.join(("1", "2","3"))
str3.join(str2)

str4 = "a" + "b"

str5 = "".join(("a","b")) # Noncompliant {{Using `str.join()` to perform a string concatenation is not energy efficient.}}

arr = ["1", "2","3"]
str6 = "".join(arr) # Noncompliant {{Using `str.join()` to perform a string concatenation is not energy efficient.}}