# Compliant
class MySlottedClass():
    __slots__ = ('a', 'b', 'c')
    def __init__(self, a, b, c):
        self.a = a
        self.b = b
        self.c = c

# Compliant
@dataclass(slots=True)
class MyDataClassSlottedClass():
    a: int
    b: int
    c: int
    def __init__(self, a, b, c):
        self.a = a
        self.b = b
        self.c = c

# Non-compliant
class MyClass(): #Noncompliant {{You should use Slots to explicitly declare data members and use way less memory than the default behavior based on __dict__ and __weakref__ attributes.}}
    def __init__(self, a, b, c):
        self.a = a
        self.b = b
        self.c = c





