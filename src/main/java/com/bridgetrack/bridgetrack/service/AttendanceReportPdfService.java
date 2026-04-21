package com.bridgetrack.bridgetrack.service;

import com.bridgetrack.bridgetrack.dto.AdminAttendanceSelectedSessionDto;
import com.bridgetrack.bridgetrack.dto.AttendancePreviewStudentDto;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.font.constants.StandardFonts;
import org.springframework.stereotype.Service;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class AttendanceReportPdfService {

    private static final DeviceRgb COLOR_NAVY      = new DeviceRgb(0x1c, 0x35, 0x57); 
    private static final DeviceRgb COLOR_HEADER_BG = new DeviceRgb(0xdf, 0xe7, 0xf2); 
    private static final DeviceRgb COLOR_ROW_ALT   = new DeviceRgb(0xfa, 0xfb, 0xfd);
    private static final DeviceRgb COLOR_BORDER    = new DeviceRgb(0xd6, 0xdc, 0xe5);
    private static final DeviceRgb COLOR_PRESENT   = new DeviceRgb(0x1f, 0x6b, 0x35);
    private static final DeviceRgb COLOR_PRESENT_BG= new DeviceRgb(0xe7, 0xf6, 0xea);
    private static final DeviceRgb COLOR_ABSENT    = new DeviceRgb(0xa1, 0x26, 0x22); 
    private static final DeviceRgb COLOR_ABSENT_BG = new DeviceRgb(0xfd, 0xec, 0xec); 
    private static final DeviceRgb COLOR_OTHER     = new DeviceRgb(0x43, 0x56, 0x6e); 
    private static final DeviceRgb COLOR_OTHER_BG  = new DeviceRgb(0xee, 0xf2, 0xf7); 
    private static final DeviceRgb COLOR_WHITE     = new DeviceRgb(255, 255, 255);
    private static final DeviceRgb COLOR_LABEL     = new DeviceRgb(0x6f, 0x7f, 0x8f); 

    public byte[] generateSessionReport(AdminAttendanceSelectedSessionDto session) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.LETTER);
        document.setMargins(50, 50, 50, 50);

        PdfFont fontBold    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        Table headerBar = new Table(UnitValue.createPercentArray(new float[]{1}))
                .setWidth(UnitValue.createPercentValue(100));

        InputStream logoStream = getClass().getResourceAsStream("/static/bridgetrack-logo.jpeg");
        Cell headerCell = new Cell()
                .setBackgroundColor(COLOR_NAVY)
                .setPadding(18)
                .setBorder(Border.NO_BORDER);

        if (logoStream != null) {
            byte[] logoBytes = logoStream.readAllBytes();
            ImageData imageData = ImageDataFactory.create(logoBytes);
            Image logo = new Image(imageData).setHeight(60).setAutoScale(false).setMarginBottom(6);
            headerCell.add(logo);
        } else {
            headerCell.add(new Paragraph("BridgeTrack")
                    .setFont(fontBold)
                    .setFontSize(20)
                    .setFontColor(COLOR_WHITE)
                    .setMargin(0));
        }

        headerCell.add(new Paragraph("Attendance Session Report")
                .setFont(fontRegular)
                .setFontSize(12)
                .setFontColor(new DeviceRgb(0xb0, 0xc8, 0xe8))
                .setMargin(0));

        headerBar.addCell(headerCell);
        document.add(headerBar);

        document.add(new Paragraph(" ").setFontSize(6)); 

        Table detailsGrid = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        addDetailCell(detailsGrid, "INSTRUCTOR", session.getInstructorName(), fontBold, fontRegular);
        addDetailCell(detailsGrid, "COURSE",     session.getCourseCode(),     fontBold, fontRegular);
        addDetailCell(detailsGrid, "SECTION",    session.getSectionLabel(),   fontBold, fontRegular);
        addDetailCell(detailsGrid, "DATE",       session.getSessionDate(),    fontBold, fontRegular);

        document.add(detailsGrid);

        document.add(new Paragraph(" ").setFontSize(8));

        Table sectionHeading = new Table(UnitValue.createPercentArray(new float[]{1}))
                .setWidth(UnitValue.createPercentValue(100));

        sectionHeading.addCell(new Cell()
                .setBackgroundColor(COLOR_HEADER_BG)
                .setBorder(new SolidBorder(COLOR_BORDER, 1))
                .setPadding(8)
                .add(new Paragraph("Session Attendance")
                        .setFont(fontBold)
                        .setFontSize(11)
                        .setFontColor(COLOR_NAVY)
                        .setMargin(0)));
        document.add(sectionHeading);

        Table attendanceTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
                .setWidth(UnitValue.createPercentValue(100));

        attendanceTable.addHeaderCell(new Cell()
                .setBackgroundColor(COLOR_HEADER_BG)
                .setBorder(new SolidBorder(COLOR_BORDER, 1))
                .setPadding(8)
                .add(new Paragraph("Student Name")
                        .setFont(fontBold).setFontSize(10).setFontColor(COLOR_NAVY).setMargin(0)));

        attendanceTable.addHeaderCell(new Cell()
                .setBackgroundColor(COLOR_HEADER_BG)
                .setBorder(new SolidBorder(COLOR_BORDER, 1))
                .setPadding(8)
                .add(new Paragraph("Status")
                        .setFont(fontBold).setFontSize(10).setFontColor(COLOR_NAVY).setMargin(0)));

        if (session.getAttendanceRecords() != null) {
            int rowIndex = 0;
            for (AttendancePreviewStudentDto record : session.getAttendanceRecords()) {
                DeviceRgb rowBg = (rowIndex % 2 == 0) ? COLOR_WHITE : COLOR_ROW_ALT;

                attendanceTable.addCell(new Cell()
                        .setBackgroundColor(rowBg)
                        .setBorder(new SolidBorder(COLOR_BORDER, 0.5f))
                        .setPadding(8)
                        .add(new Paragraph(safe(record.getStudentName()))
                                .setFont(fontRegular).setFontSize(10).setFontColor(COLOR_NAVY).setMargin(0)));

                attendanceTable.addCell(new Cell()
                        .setBackgroundColor(rowBg)
                        .setBorder(new SolidBorder(COLOR_BORDER, 0.5f))
                        .setPadding(6)
                        .add(buildStatusPill(record.getStatus(), fontBold)));

                rowIndex++;
            }
        }

        if (session.getAttendanceRecords() == null || session.getAttendanceRecords().isEmpty()) {
            attendanceTable.addCell(new Cell(1, 2)
                    .setBorder(new SolidBorder(COLOR_BORDER, 0.5f))
                    .setPadding(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .add(new Paragraph("No attendance records found for this session.")
                            .setFont(fontRegular).setFontSize(10)
                            .setFontColor(COLOR_LABEL).setMargin(0)));
        }

        document.add(attendanceTable);

       

        document.add(new Paragraph(" ").setFontSize(10));
        document.add(new Paragraph("Generated by BridgeTrack · " + java.time.LocalDate.now())
                .setFont(fontRegular)
                .setFontSize(8)
                .setFontColor(COLOR_LABEL)
                .setTextAlignment(TextAlignment.RIGHT));

        document.close();
        return baos.toByteArray();
    }


    private void addDetailCell(Table table, String label, String value,
                                PdfFont fontBold, PdfFont fontRegular) {
        table.addCell(new Cell()
                .setBorder(new SolidBorder(COLOR_BORDER, 1))
                .setBackgroundColor(COLOR_WHITE)
                .setPadding(10)
                .add(new Paragraph(label)
                        .setFont(fontBold).setFontSize(8)
                        .setFontColor(COLOR_LABEL).setMargin(0))
                .add(new Paragraph(safe(value))
                        .setFont(fontBold).setFontSize(11)
                        .setFontColor(COLOR_NAVY).setMargin(0).setMarginTop(3)));
    }


    private Paragraph buildStatusPill(String status, PdfFont fontBold) {
        String text = safe(status).toUpperCase();
        DeviceRgb textColor;
        DeviceRgb bgColor;

        switch (text) {
            case "PRESENT":
                textColor = COLOR_PRESENT;
                bgColor   = COLOR_PRESENT_BG;
                break;
            case "ABSENT":
                textColor = COLOR_ABSENT;
                bgColor   = COLOR_ABSENT_BG;
                break;
            default:
                textColor = COLOR_OTHER;
                bgColor   = COLOR_OTHER_BG;
                if (text.isEmpty()) text = "N/A";
                break;
        }

        return new Paragraph(text)
                .setFont(fontBold)
                .setFontSize(9)
                .setFontColor(textColor)
                .setBackgroundColor(bgColor, 2f, 2f, 2f, 2f)
                .setMargin(0);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}