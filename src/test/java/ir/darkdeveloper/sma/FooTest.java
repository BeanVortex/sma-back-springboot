package ir.darkdeveloper.sma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FooTest {
    
    @Test
    void getFoo(){
        Foo foo = new Foo(5);
        Foo foo2 = foo.times(5);
        assertEquals(new Foo(25), foo2);
    }

    @Test
    void getFoo2(){
        Foo foo = new Foo(10);
        assertEquals(new Foo(15), foo.times(2));
    }

}
