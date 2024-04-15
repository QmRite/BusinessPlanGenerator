package ru.urfu.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.InputStream;

@Getter
@Setter
public class SendDocumentDTO {

    private String chatId;
    private InputStream documentStream;
    private String documentName;
}
