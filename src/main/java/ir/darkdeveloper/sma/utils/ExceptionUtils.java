package ir.darkdeveloper.sma.utils;

import ir.darkdeveloper.sma.exceptions.*;
import org.hibernate.exception.DataException;

import java.util.function.Supplier;

public class ExceptionUtils {

    public static <T> T exceptionHandlers(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (DataException | BadRequestException e) {
            throw new BadRequestException(e.getLocalizedMessage());
        } catch (PasswordException e) {
            throw new PasswordException(e.getLocalizedMessage());
        } catch (ForbiddenException f) {
            throw new ForbiddenException(f.getLocalizedMessage());
        } catch (NoContentException e) {
            throw new NoContentException(e.getLocalizedMessage());
        } catch (Exception e) {
            throw new InternalException(e.getLocalizedMessage());
        }
    }
}
