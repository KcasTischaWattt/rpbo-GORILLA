package com.backend.crosswords.corpus.services;

import com.backend.crosswords.corpus.dto.CertainDigestDTO;
import com.backend.crosswords.corpus.dto.DigestsDTO;
import com.backend.crosswords.corpus.dto.DocDTO;
import com.lowagie.text.pdf.BaseFont;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {
    private final TemplateEngine templateEngine;

    public PdfService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generateDocListPdf(List<DocDTO> docList) throws Exception {
        Context context = new Context();
        context.setVariable("docList", docList);
        String html = templateEngine.process("doc-list-pdf", context);

        return convertIntoPdf(html);
    }
    public byte[] generateDigestPdf(CertainDigestDTO digest) throws Exception {
        Context context = new Context();
        context.setVariable("digest", digest);
        String html = templateEngine.process("certain-digest-pdf", context);
        return convertIntoPdf(html);
    }

    private byte[] convertIntoPdf(String html) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();

        String fontPath = getClass().getClassLoader().getResource("fonts/arial.ttf").toExternalForm();
        renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        outputStream.close();
        return outputStream.toByteArray();
    }
}
