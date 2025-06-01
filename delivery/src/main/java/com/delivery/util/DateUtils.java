package com.delivery.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

public class DateUtils {

    private DateUtils() {
        // Utility class
    }

    // Formatters
    public static final DateTimeFormatter FORMATTER_DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter FORMATTER_DD_MM_YYYY_HH_MM = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter FORMATTER_YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FORMATTER_YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter FORMATTER_ISO_LOCAL_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Obtém a data atual
     */
    public static LocalDate hoje() {
        return LocalDate.now();
    }

    /**
     * Obtém a data e hora atual
     */
    public static LocalDateTime agora() {
        return LocalDateTime.now();
    }

    /**
     * Converte string para LocalDate
     */
    public static LocalDate parseDate(String dateString, DateTimeFormatter formatter) {
        try {
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de data inválido: " + dateString, e);
        }
    }

    /**
     * Converte string para LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeString, DateTimeFormatter formatter) {
        try {
            return LocalDateTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de data/hora inválido: " + dateTimeString, e);
        }
    }

    /**
     * Formata LocalDate para string
     */
    public static String formatDate(LocalDate date, DateTimeFormatter formatter) {
        return date != null ? date.format(formatter) : null;
    }

    /**
     * Formata LocalDateTime para string
     */
    public static String formatDateTime(LocalDateTime dateTime, DateTimeFormatter formatter) {
        return dateTime != null ? dateTime.format(formatter) : null;
    }

    /**
     * Início do dia
     */
    public static LocalDateTime inicioDoDia(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * Fim do dia
     */
    public static LocalDateTime fimDoDia(LocalDate date) {
        return date.atTime(23, 59, 59, 999999999);
    }

    /**
     * Início do mês
     */
    public static LocalDate inicioDoMes(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    /**
     * Fim do mês
     */
    public static LocalDate fimDoMes(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    /**
     * Início do ano
     */
    public static LocalDate inicioDoAno(LocalDate date) {
        return date.withDayOfYear(1);
    }

    /**
     * Fim do ano
     */
    public static LocalDate fimDoAno(LocalDate date) {
        return date.withDayOfYear(date.lengthOfYear());
    }

    /**
     * Adiciona dias a uma data
     */
    public static LocalDate adicionarDias(LocalDate date, long dias) {
        return date.plusDays(dias);
    }

    /**
     * Subtrai dias de uma data
     */
    public static LocalDate subtrairDias(LocalDate date, long dias) {
        return date.minusDays(dias);
    }

    /**
     * Adiciona meses a uma data
     */
    public static LocalDate adicionarMeses(LocalDate date, long meses) {
        return date.plusMonths(meses);
    }

    /**
     * Subtrai meses de uma data
     */
    public static LocalDate subtrairMeses(LocalDate date, long meses) {
        return date.minusMonths(meses);
    }

    /**
     * Calcula diferença em dias entre duas datas
     */
    public static long diferencaEmDias(LocalDate inicio, LocalDate fim) {
        return ChronoUnit.DAYS.between(inicio, fim);
    }

    /**
     * Calcula diferença em horas entre duas datas/horas
     */
    public static long diferencaEmHoras(LocalDateTime inicio, LocalDateTime fim) {
        return ChronoUnit.HOURS.between(inicio, fim);
    }

    /**
     * Calcula diferença em minutos entre duas datas/horas
     */
    public static long diferencaEmMinutos(LocalDateTime inicio, LocalDateTime fim) {
        return ChronoUnit.MINUTES.between(inicio, fim);
    }

    /**
     * Verifica se uma data está entre duas outras datas
     */
    public static boolean estaEntre(LocalDate data, LocalDate inicio, LocalDate fim) {
        return !data.isBefore(inicio) && !data.isAfter(fim);
    }

    /**
     * Verifica se uma data/hora está entre duas outras datas/horas
     */
    public static boolean estaEntre(LocalDateTime dataHora, LocalDateTime inicio, LocalDateTime fim) {
        return !dataHora.isBefore(inicio) && !dataHora.isAfter(fim);
    }

    /**
     * Obtém o nome do mês em português
     */
    public static String nomeDoMes(int mes) {
        return switch (mes) {
            case 1 -> "Janeiro";
            case 2 -> "Fevereiro";
            case 3 -> "Março";
            case 4 -> "Abril";
            case 5 -> "Maio";
            case 6 -> "Junho";
            case 7 -> "Julho";
            case 8 -> "Agosto";
            case 9 -> "Setembro";
            case 10 -> "Outubro";
            case 11 -> "Novembro";
            case 12 -> "Dezembro";
            default -> throw new IllegalArgumentException("Mês inválido: " + mes);
        };
    }

    /**
     * Obtém o nome do dia da semana em português
     */
    public static String nomeDoDiaDaSemana(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "Segunda-feira";
            case TUESDAY -> "Terça-feira";
            case WEDNESDAY -> "Quarta-feira";
            case THURSDAY -> "Quinta-feira";
            case FRIDAY -> "Sexta-feira";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }

    /**
     * Verifica se é fim de semana
     */
    public static boolean eFimDeSemana(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    /**
     * Obtém o próximo dia útil
     */
    public static LocalDate proximoDiaUtil(LocalDate date) {
        LocalDate proximoDia = date.plusDays(1);
        while (eFimDeSemana(proximoDia)) {
            proximoDia = proximoDia.plusDays(1);
        }
        return proximoDia;
    }

    /**
     * Obtém o dia útil anterior
     */
    public static LocalDate diaUtilAnterior(LocalDate date) {
        LocalDate diaAnterior = date.minusDays(1);
        while (eFimDeSemana(diaAnterior)) {
            diaAnterior = diaAnterior.minusDays(1);
        }
        return diaAnterior;
    }

    /**
     * Calcula idade em anos
     */
    public static int calcularIdade(LocalDate dataNascimento) {
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    /**
     * Converte LocalDateTime para timestamp
     */
    public static long toTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    /**
     * Converte timestamp para LocalDateTime
     */
    public static LocalDateTime fromTimestamp(long timestamp) {
        return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.systemDefault());
    }

    /**
     * Formata duração em formato legível
     */
    public static String formatarDuracao(Duration duration) {
        long horas = duration.toHours();
        long minutos = duration.toMinutesPart();
        long segundos = duration.toSecondsPart();

        if (horas > 0) {
            return String.format("%dh %dm %ds", horas, minutos, segundos);
        } else if (minutos > 0) {
            return String.format("%dm %ds", minutos, segundos);
        } else {
            return String.format("%ds", segundos);
        }
    }

    /**
     * Obtém todas as datas de um período
     */
    public static List<LocalDate> obterDatasEntre(LocalDate inicio, LocalDate fim) {
        return inicio.datesUntil(fim.plusDays(1)).toList();
    }

    /**
     * Valida se uma string é uma data válida
     */
    public static boolean isDataValida(String dateString, DateTimeFormatter formatter) {
        try {
            LocalDate.parse(dateString, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Obtém o trimestre de uma data
     */
    public static int obterTrimestre(LocalDate date) {
        return (date.getMonthValue() - 1) / 3 + 1;
    }

    /**
     * Formata data para exibição amigável
     */
    public static String formatarDataAmigavel(LocalDateTime dateTime) {
        LocalDateTime agora = LocalDateTime.now();
        long minutos = ChronoUnit.MINUTES.between(dateTime, agora);

        if (minutos < 1) {
            return "Agora";
        } else if (minutos < 60) {
            return minutos + " minuto" + (minutos > 1 ? "s" : "") + " atrás";
        } else if (minutos < 1440) { // 24 horas
            long horas = minutos / 60;
            return horas + " hora" + (horas > 1 ? "s" : "") + " atrás";
        } else if (minutos < 10080) { // 7 dias
            long dias = minutos / 1440;
            return dias + " dia" + (dias > 1 ? "s" : "") + " atrás";
        } else {
            return formatDateTime(dateTime, FORMATTER_DD_MM_YYYY_HH_MM);
        }
    }
}