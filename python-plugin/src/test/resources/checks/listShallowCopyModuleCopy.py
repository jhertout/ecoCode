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


# List copy

list1 = [1, 2, [3, 4], 5]
list_copy1 = copy.copy(list1) # Noncompliant {{Using `copy.copy(x)` of `module copy` to perform a shallow copy of a list is not energy efficient.}}

list2 = [TestObject(1, "test1"), TestObject(2, "test2")]
list_copy2 = copy.copy(list2) # Noncompliant {{Using `copy.copy(x)` of `module copy` to perform a shallow copy of a list is not energy efficient.}}

list3 = TestObject(1, "test1").listFromObject()
list_copy3 = copy.copy(list3) # Noncompliant {{Using `copy.copy(x)` of `module copy` to perform a shallow copy of a list is not energy efficient.}}

list4 = TestObject(1, "test1").untypedlistFromObject()
list_copy4 = copy.copy(list4) # TODO {the return type of the method is not defined.. so type is ANY}

# Array copy

array1 = array.array('i', [1, 2, 3])
array_copy1 = copy.copy(array1) # NO issue
array_copy2 = copy.copy(array.array('i', [1, 2, 3])) # NO issue

## Object copy

object1 = "test"
object_copy11 = copy.copy(object1) # NO issue
object_copy12 = copy.copy("test") # NO issue

object2 = TestObject("test 1", "test 2")
object_copy21 = copy.copy(object2) # NO issue
object_copy22 = copy.copy(TestObject("test 1", "test 2")) # NO issue

# Other copy()

object3 = TestObject("test 1", "test 2")
other_list = object3.copy([1, 2]) # NO issue


it = [2,4,6,8]

def test(value:list):
    ret=copy.copy(value) # Noncompliant {{Using `copy.copy(x)` of `module copy` to perform a shallow copy of a list is not energy efficient.}}
    return ret
print(test(it))