package tobias.moreno.fin.scope.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;


@Component
@RequiredArgsConstructor
public class LocalizedMessageService {

    private final MessageSource messageSource;

    public String getMessage(String code, Locale locale) {
        return messageSource.getMessage(code, null, (locale != null) ? locale : LocaleContextHolder.getLocale());
    }

    public String getMessage(String code) {
        return getMessage(code, LocaleContextHolder.getLocale());
    }

    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}