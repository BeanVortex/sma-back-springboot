package ir.darkdeveloper.sma.utils;

import ir.darkdeveloper.sma.exceptions.BadRequestException;
import ir.darkdeveloper.sma.exceptions.ForbiddenException;
import ir.darkdeveloper.sma.exceptions.InternalException;

import java.util.function.Supplier;

public class Generics {

    public static <T> T exceptionHandlers(Supplier<T> sup) {
        try {
            return sup.get();
        } catch (ForbiddenException e) {
            throw new ForbiddenException(e);
        } catch (BadRequestException e) {
            throw new BadRequestException(e);
        } catch (Exception e) {
            throw new InternalException(e);
        }
    }
}
