package ir.darkdeveloper.sma;

public class Foo {
    
    private int amount;

    public Foo(int amount){
        this.amount = amount;
    }

    Foo times(int multiplication){
        return new Foo(amount * multiplication);
    }

    @Override
    public boolean equals(Object obj) {
        Foo foo = (Foo) obj;
        return foo.getAmount() == this.amount;
    }

    public int getAmount() {
        return amount;
    }

}
