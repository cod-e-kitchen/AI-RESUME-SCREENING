# ResumeMatch

ResumeMatch is a Spring Boot application that analyzes a candidate's resume against a job description using AI-powered information extraction and evidence-based requirement matching.

## Features

- **Resume Extraction:** Extracts structured candidate information from PDF resumes.
- **Job Description Analysis:** Identifies required skills, preferred skills, responsibilities, and qualifications.
- **Evidence-Based Matching:** Assesses each job requirement against information extracted from the resume.
- **Match Scoring:** Calculates deterministic match scores using configurable weights for required and preferred skills.
- **REST API:** Exposes endpoints for document extraction and resume-to-job matching.

## Tech Stack

- Java 21
- Spring Boot 3.5.6
- LangChain4j
- Anthropic Claude Haiku 5.5
- Apache PDFBox 3.0.6
- Maven

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/resume-api/pdf-extraction` | Extracts structured information from a resume and job description. |
| POST | `/resume-api/match` | Evaluates resume requirements and returns a match score. |

Both endpoints accept `multipart/form-data` with the following fields:

| Field | Type | Description |
|---|---|---|
| `resume` | File | Candidate resume in PDF format |
| `jobDescription` | Text | Target job description |

## Matching Methodology

ResumeMatch uses a hybrid approach:

1. **AI assessment:** Evaluates each job requirement using evidence from the extracted resume profile.
2. **Requirement classification:** Assigns one of four statuses:
   - `DIRECT_MATCH` — Explicit supporting evidence exists.
   - `PARTIAL_MATCH` — Related evidence exists, but the requirement is not fully demonstrated.
   - `NO_EVIDENCE` — Sufficient supporting evidence was not found.
   - `NOT_ASSESSABLE` — Available information is insufficient or ambiguous.
3. **Deterministic scoring:** Java calculates the overall score using configurable weights for required and preferred skills.

A missing match is not necessarily proof that a candidate lacks a skill; it may indicate that the resume does not provide sufficient evidence.

## Configuration

Configure the following properties in `application.properties` or through environment variables:

```properties
resumematch.ai.api-key=${ANTHROPIC_API_KEY}
resumematch.ai.model-name=claude-haiku-5-5
resumematch.ai.timeout-seconds=60

resumematch.matching.required-weight=0.80
resumematch.matching.preferred-weight=0.20
resumematch.matching.direct-match-score=1.0
resumematch.matching.partial-match-score=0.5
resumematch.matching.no-evidence-score=0.0
```

Set `ANTHROPIC_API_KEY` in your environment before running the application. Do not commit API keys or other secrets to version control.

## Getting Started

### Prerequisites

- Java 21 or later
- Maven
- A valid Anthropic API key

### Run Locally

Clone the repository:

```bash
git clone <repository-url>
cd <repository-directory>
```

Set your Anthropic API key, then run the application:

```bash
mvn spring-boot:run
```

The application starts on the port configured in `application.properties`.

## Testing with Postman

Send a `POST` request to:

```text
http://localhost:<port>/resume-api/match
```

Select **Body → form-data** and provide:

- `resume` — File; upload a PDF resume.
- `jobDescription` — Text; enter the target job description.

The response contains requirement-level assessments, required and preferred skill scores, an overall match score, and required skills without a full match.

## Project Scope

ResumeMatch focuses on structured resume extraction, evidence-based job requirement assessment, and transparent match scoring. The results depend on the information available in the resume and job description and should support—not replace—human hiring decisions.
