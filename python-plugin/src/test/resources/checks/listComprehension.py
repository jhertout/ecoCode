import copy
import array

class TestObject():

    def __init__(self, prop1, prop2):
        self.prop1 = prop1
        self.prop2 = prop2

    def __str__(self):
        return self.prop1 + " " +  self.prop2

    def listFromObject(self) -> list:
        return [self.prop1, self.prop2]

    def untypedlistFromObject(self) :
        return [self.prop1, self.prop2]

    def copy(self, list):
        return list

    def __init__(self, prop1):
        self.prop1 = prop1

list42 = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
list43 = [item for item in list42] # Noncompliant {{Using a list comprehension not energy efficient.}}

object = TestObject([item for item in list42]) # Noncompliant {{Using a list comprehension not energy efficient.}}

list(item for item in list42) # NO ISSUE

