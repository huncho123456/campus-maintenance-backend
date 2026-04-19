package com.praise.incident.notification;

import com.praise.incident.enums.Department;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    // Department email mappings
    public static String PPDU_EMAIL = "micheal.okafor.224056@gmail.com";
    public static String ENVIRONMENTAL_SANITATION_EMAIL = "micheal.okafor.224056@gmail.com";
    public static String ELECTRICAL_MAINTENANCE_EMAIL = "micheal.okafor.224056@gmail.com";
    public static String ICT_NETWORK_SUPPORT_EMAIL = "micheal.okafor.224056@gmail.com";
    public static String PLUMBING_AND_WATER_EMAIL = "micheal.okafor.224056@gmail.com";
    public static String GROUNDS_AND_DRAINAGE_EMAIL = "micheal.okafor.224056@gmail.com";
    public static String HOSTEL_MAINTENANCE_EMAIL = "micheal.okafor.224056@gmail.com";

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final Map<Department, String> DEPARTMENT_EMAILS = Map.of(
            Department.PPDU, PPDU_EMAIL,
            Department.ENVIRONMENTAL_SANITATION, ENVIRONMENTAL_SANITATION_EMAIL,
            Department.ELECTRICAL_MAINTENANCE, ELECTRICAL_MAINTENANCE_EMAIL,
            Department.ICT_NETWORK_SUPPORT, ICT_NETWORK_SUPPORT_EMAIL,
            Department.PLUMBING_AND_WATER, PLUMBING_AND_WATER_EMAIL,
            Department.GROUNDS_AND_DRAINAGE, GROUNDS_AND_DRAINAGE_EMAIL,
            Department.HOSTEL_MAINTENANCE, HOSTEL_MAINTENANCE_EMAIL
    );

    // ============ SHARED CSS STYLES (used by both templates) ============
    private static final String SHARED_EMAIL_STYLES = """
      <style>
        :root { color-scheme: light only; }
        * { margin:0; padding:0; box-sizing:border-box; }
        body, html {
          font-family:'Plus Jakarta Sans',sans-serif;
          background:#ffffff !important;
          color:#0f172a !important;
          padding:32px 16px;
          -webkit-text-size-adjust:100%%;
        }
        @media (prefers-color-scheme: dark) {
          body, html { background:#ffffff !important; color:#0f172a !important; }
          .card { background:#ffffff !important; border-color:#e8edf8 !important; }
          .body-section { background:#ffffff !important; }
          .row { background:#ffffff !important; color:#0f172a !important; }
          .row-even { background:#fafbff !important; }
          .rk { color:#94a3b8 !important; }
          .rv { color:#0f172a !important; }
          .note { background:#f8faff !important; }
          .foot { background:#fafbff !important; }
        }
        .inner{max-width:580px;margin:0 auto}
        .logo{text-align:center;margin-bottom:24px}
        .logo-badge{display:inline-flex;align-items:center;gap:8px;background:#f8faff !important;border:1px solid #e2e8f0;border-radius:999px;padding:8px 18px}
        .logo-dot{width:8px;height:8px;border-radius:50%%;background:#3b5bfa}
        .logo-text{font-size:12px;font-weight:600;color:#1e2a4a !important;letter-spacing:0.3px}
        .card{background:#ffffff !important;border-radius:20px;border:1.5px solid #e8edf8;overflow:hidden}
        .hero{background:#3b5bfa !important;padding:36px 40px 32px;position:relative;overflow:hidden}
        .hc{position:absolute;border-radius:50%%;border:1px solid rgba(255,255,255,0.1)}
        .hc1{width:220px;height:220px;top:-90px;right:-70px}
        .hc2{width:130px;height:130px;top:20px;right:50px}
        .chip{display:inline-flex;align-items:center;gap:6px;background:rgba(255,255,255,0.15);border:1px solid rgba(255,255,255,0.2);border-radius:999px;padding:5px 14px;margin-bottom:18px}
        .chip-dot{width:6px;height:6px;border-radius:50%%;background:#86efac}
        .chip span{font-size:10px;font-weight:700;color:#fff !important;letter-spacing:0.8px;text-transform:uppercase}
        .hero h2{color:#fff !important;font-size:24px;font-weight:700;margin:0 0 8px;line-height:1.3}
        .hero-sub{color:rgba(255,255,255,0.75) !important;font-size:13px;margin:0}
        .body-section{padding:32px 40px;background:#ffffff !important}
        .greeting{font-size:16px;font-weight:600;color:#0f172a !important;margin:0 0 8px}
        .intro{font-size:13px;color:#64748b !important;line-height:1.8;margin:0 0 24px;padding-bottom:24px;border-bottom:1.5px solid #f1f5f9}
        .st{font-size:10px;font-weight:700;color:#b0bbcc !important;text-transform:uppercase;letter-spacing:1.2px;margin:0 0 12px}
        .rows{border-radius:12px;overflow:hidden;border:1.5px solid #f0f3fa}
        .row{display:flex;justify-content:space-between;align-items:center;padding:13px 18px;border-bottom:1px solid #f8f9fd;background:#ffffff !important}
        .row-even{background:#fafbff !important}
        .row:last-child{border-bottom:none}
        .rk{font-size:12px;color:#94a3b8 !important;font-weight:500}
        .rv{font-size:12px;font-weight:600;color:#0f172a !important;text-align:right;max-width:58%%;line-height:1.6}
        .pill{display:inline-flex;align-items:center;gap:5px;padding:4px 11px;border-radius:999px;font-size:11px;font-weight:600}
        .p-low{background:#eef1ff !important;color:#3b5bfa !important}
        .p-medium{background:#fff4e6 !important;color:#d9480f !important}
        .p-high{background:#fff0f0 !important;color:#c92a2a !important}
        .p-critical{background:#fff0f0 !important;color:#c92a2a !important}
        .p-pending{background:#fff4e6 !important;color:#d9480f !important}
        .p-open{background:#eef1ff !important;color:#3b5bfa !important}
        .p-investigating{background:#eef1ff !important;color:#3b5bfa !important}
        .p-resolved{background:#ebfbee !important;color:#2f9e44 !important}
        .p-closed{background:#f1f5f9 !important;color:#64748b !important}
        .pdot{width:5px;height:5px;border-radius:50%%;background:currentColor}
        .inc-id{font-family:monospace;font-size:11px;font-weight:700;color:#3b5bfa !important;background:#eef1ff !important;padding:3px 9px;border-radius:6px}
        .cta{text-align:center;margin:28px 0 24px}
        .cta a{display:inline-block;background:#3b5bfa !important;color:#ffffff !important;text-decoration:none;padding:14px 44px;border-radius:12px;font-size:13px;font-weight:700}
        .note{background:#f8faff !important;border:1.5px solid #eef1ff;border-radius:12px;padding:16px 18px}
        .note p{font-size:12px;color:#94a3b8 !important;line-height:1.8;margin:0}
        .note strong{color:#64748b !important}
        .foot{text-align:center;padding:22px 40px 28px;border-top:1.5px solid #f1f5f9;background:#fafbff !important}
        .foot-links{display:flex;justify-content:center;gap:16px;margin-bottom:10px}
        .foot-links a{font-size:11px;color:#94a3b8 !important;text-decoration:none;font-weight:500}
        .foot p{font-size:11px;color:#b0bec5 !important;line-height:2;margin:0}
        .foot strong{color:#90a4ae !important}
      </style>
    """;

    public void sendIncidentNotifications(NotificationDto dto) {
        notifyDepartment(dto);
        notifyReporter(dto);
    }

    private void notifyDepartment(NotificationDto incident) {
        String departmentEmail = DEPARTMENT_EMAILS.get(incident.getResolvingDepartment());
        if (departmentEmail == null) {
            log.warn("No email mapped for department: {}", incident.getResolvingDepartment());
            return;
        }

        String subject = String.format(
                "🚨 New Incident Assigned to %s • %s",
                formatDepartmentName(incident.getResolvingDepartment()),
                incident.getIncidentNumber()
        );

        String body = buildDepartmentNewIncidentEmail(incident);

        sendEmail(departmentEmail, subject, body);
        log.info("Department notification sent to {} for incident {}", departmentEmail, incident.getIncidentNumber());
    }

    private void notifyReporter(NotificationDto dto) {
        String subject = String.format(
                "Your Incident %s Has Been Received",
                dto.getIncidentNumber()
        );
        String body = buildUserNewIncidentEmail(dto);

        sendEmail(dto.getReportedByEmail(), subject, body);
        log.info("Reporter notification sent to {} for incident {}", dto.getReportedByEmail(), dto.getIncidentNumber());
    }

    public void sendStatusUpdateNotification(NotificationDto dto) {
        String departmentEmail = DEPARTMENT_EMAILS.get(dto.getResolvingDepartment());
        String subject = String.format(
                "🔄 Incident %s Update: Now %s",
                dto.getIncidentNumber(),
                formatPriority(dto.getStatus())
        );

        // Send to department
        sendEmail(departmentEmail, subject, buildDepartmentStatusUpdateEmail(dto));
        log.info("Status update sent to department {} for incident {}", departmentEmail, dto.getIncidentNumber());

        // Send to reporter
        sendEmail(dto.getReportedByEmail(), subject, buildUserStatusUpdateEmail(dto));
        log.info("Status update sent to reporter {} for incident {}", dto.getReportedByEmail(), dto.getIncidentNumber());
    }

    // ============ DEPARTMENT EMAIL TEMPLATES ============

    private String buildDepartmentNewIncidentEmail(NotificationDto dto) {
        String priorityClass = dto.getPriority().toString().toLowerCase();
        String statusClass = dto.getStatus().toString().toLowerCase().replace("_", "-");
        String updatedAt = dto.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));

        return """
    <!DOCTYPE html>
    <html lang="en" style="background:#ffffff !important">
    <head>
      <meta charset="UTF-8"/>
      <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
      <meta name="color-scheme" content="light only"/>
      <meta name="supported-color-schemes" content="light"/>
      <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet"/>
      %s
    </head>
    <body style="background:#ffffff !important;margin:0;padding:32px 16px">
    <div class="inner">
      <div class="logo">
        <div class="logo-badge">
          <div class="logo-dot"></div>
          <span class="logo-text">Campus Maintenance and Fault Reporting System</span>
        </div>
      </div>
      <div class="card">
        <div class="hero">
          <div class="hc hc1"></div><div class="hc hc2"></div>
          <div class="chip"><div class="chip-dot"></div><span>New Assignment</span></div>
          <h2>New incident<br>assigned to your team</h2>
          <p class="hero-sub">A new incident requires your attention.</p>
        </div>
        <div class="body-section">
          <p class="greeting">Hello, %s Team</p>
          <p class="intro">A new incident has been assigned to your department. Please review the details below and take appropriate action.</p>
          <p class="st">Incident details</p>
          <div class="rows">
            <div class="row"><span class="rk">Incident number</span><span class="rv"><span class="inc-id">%s</span></span></div>
            <div class="row row-even"><span class="rk">Title</span><span class="rv">%s</span></div>
            <div class="row"><span class="rk">Description</span><span class="rv">%s</span></div>
            <div class="row row-even"><span class="rk">Priority</span><span class="rv"><span class="pill p-%s"><span class="pdot"></span>%s</span></span></div>
            <div class="row"><span class="rk">Location</span><span class="rv">%s</span></div>
            <div class="row row-even"><span class="rk">Reported by</span><span class="rv">%s</span></div>
            <div class="row"><span class="rk">Status</span><span class="rv"><span class="pill p-%s"><span class="pdot"></span>%s</span></span></div>
            <div class="row"><span class="rk">Date & time</span><span class="rv">%s</span></div>
          </div>
          <div class="cta"><a href="http://localhost:5173/admin-login">View incident on portal &rarr;</a></div>
          <div class="note"><p>You'll receive updates as the status changes. For urgent concerns, contact <strong>support@yourcompany.com</strong> directly.</p></div>
        </div>
        <div class="foot">
          <div class="foot-links">
            <a href="#">Help Center</a>
            <a href="#">Contact Support</a>
            <a href="#">Unsubscribe</a>
          </div>
          <p>This is an automated notification — please do not reply to this email.</p>
          <p><strong>Campus Maintenance and Fault Reporting System</strong> &nbsp;&middot;&nbsp; support@yourcompany.com</p>
        </div>
      </div>
    </div>
    </body></html>
        """.formatted(
                SHARED_EMAIL_STYLES,
                formatDepartmentName(dto.getResolvingDepartment()),
                dto.getIncidentNumber(), dto.getTitle(), dto.getDescription(),
                priorityClass, formatPriority(dto.getPriority()),
                dto.getLocation(), dto.getReportedByEmail(),
                statusClass, formatPriority(dto.getStatus()),
                updatedAt
        );
    }

    private String buildDepartmentStatusUpdateEmail(NotificationDto dto) {
        String priorityClass = dto.getPriority().toString().toLowerCase();
        String statusClass = dto.getStatus().toString().toLowerCase().replace("_", "-");
        String updatedAt = dto.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));

        return """
    <!DOCTYPE html>
    <html lang="en" style="background:#ffffff !important">
    <head>
      <meta charset="UTF-8"/>
      <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
      <meta name="color-scheme" content="light only"/>
      <meta name="supported-color-schemes" content="light"/>
      <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet"/>
      %s
    </head>
    <body style="background:#ffffff !important;margin:0;padding:32px 16px">
    <div class="inner">
      <div class="logo">
        <div class="logo-badge">
          <div class="logo-dot"></div>
          <span class="logo-text">Campus Maintenance and Fault Reporting System</span>
        </div>
      </div>
      <div class="card">
        <div class="hero">
          <div class="hc hc1"></div><div class="hc hc2"></div>
          <div class="chip"><div class="chip-dot"></div><span>Status Updated</span></div>
          <h2>Incident status<br>has been updated</h2>
          <p class="hero-sub">We're keeping you informed every step of the way.</p>
        </div>
        <div class="body-section">
          <p class="greeting">Hello, %s Team</p>
          <p class="intro">An incident assigned to your department has had a status change. Please review the updated details below and take any necessary action.</p>
          <p class="st">Incident details</p>
          <div class="rows">
            <div class="row"><span class="rk">Incident number</span><span class="rv"><span class="inc-id">%s</span></span></div>
            <div class="row row-even"><span class="rk">Title</span><span class="rv">%s</span></div>
            <div class="row"><span class="rk">Description</span><span class="rv">%s</span></div>
            <div class="row row-even"><span class="rk">Priority</span><span class="rv"><span class="pill p-%s"><span class="pdot"></span>%s</span></span></div>
            <div class="row"><span class="rk">Location</span><span class="rv">%s</span></div>
            <div class="row row-even"><span class="rk">Reported by</span><span class="rv">%s</span></div>
            <div class="row"><span class="rk">New status</span><span class="rv"><span class="pill p-%s"><span class="pdot"></span>%s</span></span></div>
            <div class="row"><span class="rk">Updated at</span><span class="rv">%s</span></div>
          </div>
          <div class="cta"><a href="http://localhost:5173/admin-login?">View incident on portal &rarr;</a></div>
          <div class="note"><p>Continue monitoring this incident. Contact <strong>support@yourcompany.com</strong> if you need assistance.</p></div>
        </div>
        <div class="foot">
          <div class="foot-links">
            <a href="#">Help Center</a>
            <a href="#">Contact Support</a>
            <a href="#">Unsubscribe</a>
          </div>
          <p>This is an automated notification — please do not reply to this email.</p>
          <p><strong>Campus Maintenance and Fault Reporting System</strong> &nbsp;&middot;&nbsp; support@yourcompany.com</p>
        </div>
      </div>
    </div>
    </body></html>
        """.formatted(
                SHARED_EMAIL_STYLES,
                formatDepartmentName(dto.getResolvingDepartment()),
                dto.getIncidentNumber(), dto.getTitle(), dto.getDescription(),
                priorityClass, formatPriority(dto.getPriority()),
                dto.getLocation(), dto.getReportedByEmail(),
                statusClass, formatPriority(dto.getStatus()),
                updatedAt
        );
    }

    // ============ USER EMAIL TEMPLATES ============

    private String buildUserNewIncidentEmail(NotificationDto dto) {
        String priorityClass = dto.getPriority().toString().toLowerCase();
        String statusClass = dto.getStatus().toString().toLowerCase().replace("_", "-");
        String updatedAt = dto.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));

        return """
    <!DOCTYPE html>
    <html lang="en" style="background:#ffffff !important">
    <head>
      <meta charset="UTF-8"/>
      <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
      <meta name="color-scheme" content="light only"/>
      <meta name="supported-color-schemes" content="light"/>
      <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet"/>
      %s
    </head>
    <body style="background:#ffffff !important;margin:0;padding:32px 16px">
    <div class="inner">
      <div class="logo">
        <div class="logo-badge">
          <div class="logo-dot"></div>
          <span class="logo-text">Campus Maintenance and Fault Reporting System</span>
        </div>
      </div>
      <div class="card">
        <div class="hero">
          <div class="hc hc1"></div><div class="hc hc2"></div>
          <div class="chip"><div class="chip-dot"></div><span>Incident Received</span></div>
          <h2>Your incident has been<br>successfully submitted</h2>
          <p class="hero-sub">We've received your report and will keep you updated.</p>
        </div>
        <div class="body-section">
          <p class="greeting">Hello, %s</p>
          <p class="intro">Your incident has been successfully submitted and is now under review. Here's a summary of your submission.</p>
          <p class="st">Submission summary</p>
          <div class="rows">
            <div class="row"><span class="rk">Incident number</span><span class="rv"><span class="inc-id">%s</span></span></div>
            <div class="row row-even"><span class="rk">Title</span><span class="rv">%s</span></div>
            <div class="row"><span class="rk">Description</span><span class="rv">%s</span></div>
            <div class="row row-even"><span class="rk">Priority</span><span class="rv"><span class="pill p-%s"><span class="pdot"></span>%s</span></span></div>
            <div class="row"><span class="rk">Location</span><span class="rv">%s</span></div>
            <div class="row row-even"><span class="rk">Assigned department</span><span class="rv">%s</span></div>
            <div class="row"><span class="rk">Current status</span><span class="rv"><span class="pill p-%s"><span class="pdot"></span>%s</span></span></div>
            <div class="row"><span class="rk">Submitted at</span><span class="rv">%s</span></div>
          </div>
          <div class="cta"><a href="http://localhost:5173/signin?">Track your incident &rarr;</a></div>
          <div class="note"><p>You'll receive email updates whenever the status changes. For urgent concerns, contact <strong>support@yourcompany.com</strong>.</p></div>
        </div>
        <div class="foot">
          <div class="foot-links">
            <a href="#">Help Center</a>
            <a href="#">Contact Support</a>
            <a href="#">Unsubscribe</a>
          </div>
          <p>This is an automated notification — please do not reply to this email.</p>
          <p><strong>Campus Maintenance and Fault Reporting System</strong> &nbsp;&middot;&nbsp; support@yourcompany.com</p>
        </div>
      </div>
    </div>
    </body></html>
        """.formatted(
                SHARED_EMAIL_STYLES,
                dto.getReporterName(),
                dto.getIncidentNumber(), dto.getTitle(), dto.getDescription(),
                priorityClass, formatPriority(dto.getPriority()),
                dto.getLocation(), formatDepartmentName(dto.getResolvingDepartment()),
                statusClass, formatPriority(dto.getStatus()),
                updatedAt
        );
    }

    private String buildUserStatusUpdateEmail(NotificationDto dto) {
        String priorityClass = dto.getPriority().toString().toLowerCase();
        String statusClass = dto.getStatus().toString().toLowerCase().replace("_", "-");
        String updatedAt = dto.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));

        return """
    <!DOCTYPE html>
    <html lang="en" style="background:#ffffff !important">
    <head>
      <meta charset="UTF-8"/>
      <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
      <meta name="color-scheme" content="light only"/>
      <meta name="supported-color-schemes" content="light"/>
      <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet"/>
      %s
    </head>
    <body style="background:#ffffff !important;margin:0;padding:32px 16px">
    <div class="inner">
      <div class="logo">
        <div class="logo-badge">
          <div class="logo-dot"></div>
          <span class="logo-text">Campus Maintenance and Fault Reporting System</span>
        </div>
      </div>
      <div class="card">
        <div class="hero">
          <div class="hc hc1"></div><div class="hc hc2"></div>
          <div class="chip"><div class="chip-dot"></div><span>Status Updated</span></div>
          <h2>Your incident has<br>been updated</h2>
          <p class="hero-sub">We're keeping you informed every step of the way.</p>
        </div>
        <div class="body-section">
          <p class="greeting">Hello, %s</p>
          <p class="intro">The status of your reported incident has changed. Here's a full summary — please review and reach out if you have any concerns.</p>
          <p class="st">Incident summary</p>
          <div class="rows">
            <div class="row"><span class="rk">Incident number</span><span class="rv"><span class="inc-id">%s</span></span></div>
            <div class="row row-even"><span class="rk">Title</span><span class="rv">%s</span></div>
            <div class="row"><span class="rk">Description</span><span class="rv">%s</span></div>
            <div class="row row-even"><span class="rk">Priority</span><span class="rv"><span class="pill p-%s"><span class="pdot"></span>%s</span></span></div>
            <div class="row"><span class="rk">Location</span><span class="rv">%s</span></div>
            <div class="row row-even"><span class="rk">Assigned department</span><span class="rv">%s</span></div>
            <div class="row"><span class="rk">Updated status</span><span class="rv"><span class="pill p-%s"><span class="pdot"></span>%s</span></span></div>
            <div class="row"><span class="rk">Updated at</span><span class="rv">%s</span></div>
          </div>
         <div class="cta"><a href="http://localhost:5173/signin?">View full details &rarr;</a></div>
          <div class="note"><p>We'll continue to notify you of any further changes. Questions? Contact <strong>support@yourcompany.com</strong>.</p></div>
        </div>
        <div class="foot">
          <div class="foot-links">
            <a href="#">Help Center</a>
            <a href="#">Contact Support</a>
            <a href="#">Unsubscribe</a>
          </div>
          <p>This is an automated notification — please do not reply to this email.</p>
          <p><strong>Campus Maintenance and Fault Reporting System</strong> &nbsp;&middot;&nbsp; support@yourcompany.com</p>
        </div>
      </div>
    </div>
    </body></html>
        """.formatted(
                SHARED_EMAIL_STYLES,
                dto.getReporterName(),
                dto.getIncidentNumber(), dto.getTitle(), dto.getDescription(),
                priorityClass, formatPriority(dto.getPriority()),
                dto.getLocation(), formatDepartmentName(dto.getResolvingDepartment()),
                statusClass, formatPriority(dto.getStatus()),
                updatedAt
        );
    }

    // ============ EMAIL SENDING ============

    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("Campus Maintenance and Fault Reporting System <" + fromEmail + ">");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    // ============ FORMATTING UTILS ============

    private String formatDepartmentName(Department dept) {
        if (dept == null) return "N/A";
        return Arrays.stream(dept.name().split("_"))
                .map(word -> word.charAt(0) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    private String formatPriority(Object priority) {
        if (priority == null) return "N/A";
        String p = priority.toString().toLowerCase();
        return p.substring(0, 1).toUpperCase() + p.substring(1);
    }
}