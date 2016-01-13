\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{mtxprint}[2006/08/03 MmTeX print output]

% ---------------------------------------------------------------------------
% General
% ---------------------------------------------------------------------------

% Inherit from class "article":
\LoadClass{article}

% Textsizes in pt:
% +----------+--------------+-----------+
% | Name     | nominal size | real size |
% +----------+--------------+-----------+
% |          |      5       |    5      |
% |          |      6       |    6      |
% |          |      7       |    7      |
% |          |      8       |    8      |
% |          |      9       |    9      |
% |          |     10       |   10      |
% |          |     11       |   11.95   |
% | normal   |     12       |   12      |
% | large    |     14       |   14.4    |
% | Large    |     17       |   17.28   |
% | LARGE    |     20       |   20.74   |
% | huge     |     25       |   24.88   |
% +----------+--------------+-----------+

% Textsize commands:
\renewcommand{\normalsize}{\fontsize{12pt}{14pt}\selectfont}
\renewcommand{\large}{\fontsize{14.4pt}{18pt}\selectfont}
\renewcommand{\Large}{\fontsize{17.28pt}{22pt}\selectfont}
\renewcommand{\LARGE}{\fontsize{20.74pt}{25pt}\selectfont}
\renewcommand{\huge}{\fontsize{24.88pt}{30pt}\selectfont}

% Activate normalsize:
\normalsize

% Paper and text size:
\setlength{\paperheight}{297mm}
\setlength{\paperwidth}{210mm}
\setlength{\textwidth}{155mm}
\setlength{\textheight}{230mm}

% Paragraph style:
\setlength{\parskip}{0.5\baselineskip}
\setlength{\parindent}{0em}

% Command "\thepage"
\renewcommand{\thepage}{\arabic{page}}

% More space between table rows:
\newcommand{\defaultArrayStratch}{1.25}
\renewcommand{\arraystretch}{\defaultArrayStratch}

% ---------------------------------------------------------------------------
% Title, authors, version, etc.
% ---------------------------------------------------------------------------

\newcommand{\headItemSep}{1.5\baselineskip}

% Environment "titleAndSubtitle"
\newenvironment{titleAndSubtitle}
{\vspace{0.0\baselineskip}}
{\vspace{0.0\baselineskip}}

% Command "\title":
\renewcommand{\title}[1]
{{\huge\bfseries #1}\vspace{0.2\baselineskip}}

% Command "\subtitle":
\newcommand{\subtitle}[1]
{{\\[0.5\baselineskip] \large\bfseries #1}}

% Environment "authors"
\newenvironment{authors}
{\vspace{1.5\baselineskip}
 \setlength{\parskip}{0.1\baselineskip}}
{\vspace{0.0\baselineskip}}

% Command "\author":
\renewcommand{\author}[1]{#1\par}

% Command "\authorWithEmail":
\newcommand{\authorWithEmail}[2]
{#1 $<$#2$>$\par}

% Command "\version"
\newcommand{\version}[1]
{\vspace{1.5\baselineskip}
 #1
 \vspace{0.0\baselineskip}}

% ---------------------------------------------------------------------------
% Table of contents
% ---------------------------------------------------------------------------

\newcommand{\tocHeadline}[1]%
{\vspace{1.5\baselineskip}
 \textbf{\Large #1}
 \vspace{0.5\baselineskip}}

%\newcommand{\tocItem}[3]%
%{\item[#1] #2 \dotfill \pageref{#3}}

\newenvironment{tocItems}
{\begin{list}{}
  {\topsep0.1\baselineskip
   \partopsep0ex
   \itemsep0.1\baselineskip
   \itemindent0em
   \labelwidth1.5em
   \labelsep0.5em
   \leftmargin2.0em}}
{\end{list}}

% ---------------------------------------------------------------------------
% Simple markup
% ---------------------------------------------------------------------------

\newcommand{\mtxEmph}[1]{\textit{#1}}
\newcommand{\mtxCode}[1]{\texttt{#1}}
\newcommand{\mtxKeyb}[1]{\fbox{#1}}
\newcommand{\mtxVar}[1]{\textit{#1}}
\newcommand{\mtxFile}[1]{\textit{#1}}
\newcommand{\mtxMeta}[1]{\textit{#1}}

% ---------------------------------------------------------------------------
% Sections, subsections, subsubsections
% ---------------------------------------------------------------------------

\newcommand{\secNumAndHeadline}[2]
{\par\vspace{2.0\baselineskip}
 {\Large\bfseries #1\hspace{0.5em}#2}
 \par\vspace{0.5\baselineskip}}

\newcommand{\secHeadline}[1]
{\par\vspace{2.0\baselineskip}
 {\Large\bfseries #1}
 \par\vspace{0.5\baselineskip}}

\newcommand{\subsecNumAndHeadline}[2]
{\par\vspace{1.0\baselineskip}
 {\large\bfseries #1\hspace{0.5em}#2}
 \par\vspace{0.25\baselineskip}}

\newcommand{\subsecHeadline}[1]
{\par\vspace{1.0\baselineskip}
 {\large\bfseries #1}
 \par\vspace{0.25\baselineskip}}

\newcommand{\subsubsecNumAndHeadline}[2]
{\par\vspace{1.0\baselineskip}
 {\normalsize\bfseries #1\hspace{0.5em}#2}
 \par\vspace{0.125\baselineskip}}

\newcommand{\subsubsecHeadline}[2]
{\par\vspace{1.0\baselineskip}
 {\normalsize\bfseries #1}
 \par\vspace{0.125\baselineskip}}

% ---------------------------------------------------------------------------
% Tables
% ---------------------------------------------------------------------------

\newenvironment{mtxTable}[1]%
{\par%
 \vspace{0.25\baselineskip}%
 \begin{tabular}{#1}}%
{\end{tabular}%
 \par%
 \vspace{0.25\baselineskip}}

% ---------------------------------------------------------------------------
% Lists
% ---------------------------------------------------------------------------

\newenvironment{mtxList}
{\begin{list}{}
  {\topsep0.1\baselineskip
   \partopsep0ex
   \itemsep0.1\baselineskip
   \itemindent0em
   \labelwidth1.5em
   \labelsep0.5em
   \leftmargin2.0em}}
{\end{list}}

\newcommand{\listItemMarkI}{$\bullet$}
\newcommand{\listItemMarkII}{---}
\newcommand{\listItemMarkIII}{*}
\newcommand{\listItemMarkIV}{$\cdot$}
\newcommand{\listItemMarkOther}{-}

% ---------------------------------------------------------------------------
% "Hyperlinks"
% ---------------------------------------------------------------------------

\newcommand{\mtxRef}[1]{\underline{#1}}

% ---------------------------------------------------------------------------
% Preformatted
% ---------------------------------------------------------------------------


\newenvironment{mtxPreformatted}%
{\par%
 \vspace{0.25\baselineskip}%
 \raggedright\ttfamily}%
{\par%
 \vspace{0.25\baselineskip}}
