package com.bridgetrack.bridgetrack.service;

import com.bridgetrack.bridgetrack.dto.InstructorRosterStudentDto;
import com.bridgetrack.bridgetrack.dto.InstructorSectionOptionDto;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class RosterPdfService {
	
	private String safe(Object value) {
	    return value == null ? "" : String.valueOf(value);
	}

    public byte[] generateRoster(
            InstructorSectionOptionDto section,
            List<InstructorRosterStudentDto> students) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // TITLE
            document.add(new Paragraph("BridgeTrack Class Roster")
                    .setBold()
                    .setFontSize(18));

            document.add(new Paragraph("\n"));

            // SECTION INFO
            document.add(new Paragraph("Course: " + safe(section.getCourseName())));            
            document.add(new Paragraph("Section ID: " + section.getSectionId()));
            document.add(new Paragraph("Section ID: " + section.getSectionId()));
            document.add(new Paragraph("\n"));

            // STUDENT COUNT
            document.add(new Paragraph("Total Students: " + students.size()));
            document.add(new Paragraph("\n"));

            // TABLE
            float[] columnWidths = {150, 150, 200, 120, 100};
            Table table = new Table(columnWidths);

            table.addHeaderCell("First Name");
            table.addHeaderCell("Last Name");
            table.addHeaderCell("Email");
            table.addHeaderCell("Phone");
            table.addHeaderCell("Status");

            for (InstructorRosterStudentDto student : students) {
            	table.addCell(safe(student.getFirstName()));
            	table.addCell(safe(student.getLastName()));
            	table.addCell(safe(student.getStudentEmail()));
            	table.addCell(safe(student.getPhone()));
            	table.addCell(safe(student.getStatus()));
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}