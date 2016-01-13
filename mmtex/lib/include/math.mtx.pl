package Mumie::MmTeX::Converter;

# Authors:  Tilman Rassy <rassy@math.tu-berlin.de>,
#           Christian Ruppert <ruppert@math.tu-berlin.de>
#
# $Id: math.mtx.pl,v 1.127 2008/01/20 22:41:54 rassy Exp $

# The MIT License (MIT)
# 
# Copyright (c) 2010 Technische Universitaet Berlin
# 
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
# 
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
# 
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

require Mumie::MmTeX::Parser;
use Mumie::Boolconst;
use Mumie::Text qw(/.+/);
use Mumie::Scanner qw(/access_scan_proc_list/);

# ==========================================================================================
# h1: General
# ==========================================================================================

# ------------------------------------------------------------------------------------------
# h2: Description
# ------------------------------------------------------------------------------------------
#
# Math support
#
# ------------------------------------------------------------------------------------------
# h2: Math nodes and math node lists
# ------------------------------------------------------------------------------------------
#
# A mathematical formular is represented by so-called math nodes. A math node may represent
# a single symbol or a compound of symbols. The whole formula is represented by a list
# (array) of math nodes, which is in fact a tree, since some of the nodes may contain other
# math node lists.
#
# A math node itself is a hash containing the following:
#
#H
#   type        & Type of the node (see below). \\
#   value       & The value of the node. May be a string, a reference to another
#                 node, or a reference to a math node list. Optional.\\
#   subscript   & Reference to another math node with is the subscript of the
#                 current node. Optional.\\
#   superscript & Reference to another math node with is the superscript of
#                 the current node. Optional. \\
#   scripts_as_limits
#               & Whether sub- and superscripts should be displayed as limits,
#                 i.e., below  and above the symbol. \\
#   left_paren  & Left parenthesis. A string. Optional. \\
#   right_paren & Right parenthesis. A string. Optional. \\
#   source      & Reference to the source table, a hash containing information
#                 about the source (see below).
#/H
#
# The source table contains at least the following entries:
#
#H
#   scan_proc   & Reference to the scan process that produced this math mode. \\
#   pos         & Source position that corresponds to this math node.
#/H
#
# There is always a current math node list. It is accessible through the reference
# $scan_proc->{math_node_list} pointing to it.

# ------------------------------------------------------------------------------------------
# h2: Math node types
# ------------------------------------------------------------------------------------------
#
# Each math node has a type. Types are specified by strings. The following types are
# defined:
#
#H
#  identifier         & Math variable, in the typesetting sence, i.e., a non-number
#                       and non-operator. \\
#  number             & Math constant, in the typesetting sense, i.e., a number. \\
#  operator           & Math operator, in the typesetting sense, i.e., "+", "-",
#                       etc., but may also be an operator that doesn't have an
#                       ascii character, for example the vector product sign. \\
#  separator          & Punctuation to separate symbols, i.e. ".", ",", etc. \\
#  hspace             & Extra horizontal space \\
#  block              & Groups together several math nodes. For a block that
#                       arises from an expression in parenthesis, the "left_paren"
#                       and "right_paren" entries of the node should be set. \\
#  fraction           & Math fraction.
#  accented           & Accented expression (expression with e.g. a bar or tilde
#                       above it)
#  inner_product      & Inner product
#  normal_text        & Normal (non-mathematical) text
#/H

# ------------------------------------------------------------------------------------------
# h2: The math node table
# ------------------------------------------------------------------------------------------
#
# For each math node type, mmtex stores some information it needs to deal with nodes of
# that type. These informations are stored in the so-called math node table, which is a
# hash mapping each math node type to (a references to) a hash containing the
# information. The hash contains at least the following entries:
#
#H
#  value_type      & Type of the value. Possible values are "TEXT" (meaning the
#                    value is a string), "NODE" (meaning the value is a reference to
#                    another node), "NODE_LIST" (meaning the value is a reference to
#                    another  node list), "OUTPUT_LIST" (meaning the value is a
#                    reference to an output list), "SPECIAL" (can be anything). \\
#  value_handler   & Reference to a function called to handle nodes of this type.\\
#  has_limits      & Whether nodes of this type typically have limits (i.e., sum
#                    and integral nodes).
#/H

# ------------------------------------------------------------------------------------------
# h2: How the math node list is written to output
# ------------------------------------------------------------------------------------------
#
# After the representation of a formula as a math node list is complete, it must be appended
# to the ouput list. Essentially, this is done per node: each math node type has a so-called
# value handler (see key "value_handler" in the math node table), which is a function that
# is responsible to convert the node to XML and append it to the output list. The value
# handler is called with one argument, the node in question.

# ------------------------------------------------------------------------------------------
# h2: Additional fields in $scan_proc
# ------------------------------------------------------------------------------------------
#H
#  math_nodes        & The math node table. \\
#  math_display      & Whether in inline or block math at the current scan position. The
#                      two corresponding values are '"INLINE"'
#                      and '"BLOCK"', respectively. \\
#  math_mode         & Whether inside a base, subscript, or superscript expression. The
#                      corresponding values are '"BASE"', '"SUBSCRIPT"',
#                      and "'SUPERSCRIPT"'.
#  math_paren_mode   & Default math parenthesis mode. May be 'BLOCK' or 'SINGLE'. If
#                      'BLOCK', parenthesis are treated as starters/closers of math blocks.
#                      Otherwise, they are simply symbols. \\
#  next_math_paren_mode &
#                      Parenthesis mode to apply to the next parenthesis. \\
#  math_left_paren   & Stores the last left parentheses for later use. \\
#  math_node_list    & The current math node list (see below).
#  start_inline_math_begin_hook &
#                      Reference to a function run by `start_inline_math` as
#                      the the very first thing it does. \\
#  start_inline_math_end_hook &
#                      Reference to a function run by `start_inline_math` as
#                      the the very last thing it does. \\
#  close_inline_math_begin_hook &
#                      Reference to a function run by `close_inline_math` as
#                      the the very first thing it does. \\
#  close_inline_math_end_hook &
#                      Reference to a function run by `close_inline_math` as
#                      the the very last thing it does.
#/H

# ------------------------------------------------------------------------------------------
# h2: Math token types
# ------------------------------------------------------------------------------------------
#H
#  inline_math_boundry  & Start or end of inline math \\
#  math_identifier      & Math variable \\
#  math_number          & Math number \\
#  math_op              & Math operator \\
#  math_punct           & Math punctuation \\
#  math_block_start     & Beginning of a {} block \\
#  math_block_end       & End of a {} block \\
#  math_left_paren      & Math left parenthesis \\
#  math_right_paren     & Math right parenthesis \\
#  math_subscr          & Math subscript \\
#  math_superscr        & Math superscript
#  math_one_char_cmd    & Command consisting of one character \\
#/H

# ------------------------------------------------------------------------------------------
# h2: Namespace
# ------------------------------------------------------------------------------------------
#
# If desired, the MathML namespace and/or a prefix for the namespace can be set. this is
# controlled by the following global variables:
#L
#  - $mathml_namespace \\
#  - $mathml_namespace_prefix \\
#  - $set_mathml_namespace \\
#  - $set_mathml_namespace_prefix
#/L
# $mathml_namespace and $mathml_namespace_prefix contain the namespace and the corresponding
# prefix, respectively. If $set_mathml_namespace_prefix has a true value, the prefix is
# added to each MathML tag, including the "math" root tag. If $set_mathml_namespace has a
# true value, the "math" root tag gets a "xmlns:"$mathml_namespace_prefix attribute if
# $set_mathml_namespace_prefix also has a true value and a "xmlns" attribute if
# $set_mathml_namespace_prefix has a false value. In both cases, the attribute is set to
# $mathml_namespace.
#
# By default, $set_mathml_namespace and $set_mathml_namespace_prefix are both false.
#
# Examples: Assume $mathml_namespace = "http://www.w3.org/1998/Math/MathML" and
# $mathml_namespace_prefix = "math". If $set_mathml_namespace and
# $set_mathml_namespace_prefix both have a true value, the formula 1+x would be coded as:
#c
#  <math:math xmlns:math="http://www.w3.org/1998/Math/MathML" mode="inline">
#    <math:mn>1</math:mn>
#    <math:mo>+</math:mo>
#    <math:mi>x</math:mi>
#  </math:math>
#/c
# If $set_mathml_namespace has a true and $set_mathml_namespace_prefix a false value, the
# code would be:
#c
#  <math xmlns="http://www.w3.org/1998/Math/MathML" mode="inline">
#    <mn>1</mn>
#    <mo>+</mo>
#    <mi>x</mi>
#  </math>
#/c
# And if $set_mathml_namespace has a false and $set_mathml_namespace_prefix a true value, the
# code would be:
#c
#  <math:math mode="inline">
#    <math:mn>1</math:mn>
#    <math:mo>+</math:mo>
#    <math:mi>x</math:mi>
#  </math:math>
#/c

# ------------------------------------------------------------------------------------------
# h2: Parenthesis mode
# ------------------------------------------------------------------------------------------
# There is a global vriable $math_paren_mode that specifies how parenthesized expressions
# are described via MathML. Recall that MathML knows two possibilities to desribe
# parenthesized expressions:
#c
#  <mrow><mo>(</mo> ... <mo>)</mo></mrow>
#/c
# and
#c
#  <mfenced open="(" close=")"> ... </mfenced>
#/c
# A value of "PLAIN" for $mathml_paren_mode means the first method, a value of "MFENCED"
# the second.

log_message("\nLibrary math ", '$Revision: 1.127 $ ', "\n");

# ==========================================================================================
# h1: Central fuctionalities
# ==========================================================================================

# ------------------------------------------------------------------------------------------
# h2: Misc. data
# ------------------------------------------------------------------------------------------

my %cmds_vs_chars
  = (
     '|' => '&DoubleVerticalBar;',
     'aleph' => '&aleph;',
     'alpha' => '&alpha;',
     'amalg' => '&amalg;',
     'backslash' => '\\',
     'wedge' => '&and;',
     'angle' => '&ang;',
     'measuredangle' => '&angmsd;',
     'sphericalangle' => '&angsph;',
     'approx' => '&ap;',
     'approxeq' => '&ape;',
     'ast' => '&ast;',
     'asymp' => '&asymp;',
     'barwedge' => '&barwed;',
     'doublebarwedge' => '&Barwed;',
     'backcong' => '&bcong;',
     'because' => '&becaus;',
     'backepsilon' => '&bepsi;',
     'beta' => '&beta;',
     'beth' => '&beth;',
     'bot' => '&bottom;',
     'bowtie' => '&bowtie;',
     'backprime' => '&bprime;',
     'backsim' => '&bsim;',
     'backsimeq' => '&bsime;',
     'backslash' => '&bsol;',
     'bullet' => '&bull;',
     'Bumpeq' => '&bump;',
     'bumpeq' => '&bumpe;',
     'cap' => '&cap;',
     'Cap' => '&Cap;',
     'checkmark' => '&check;',
     'chi' => '&chi;',
     'circ' => '&cir;',
     'circeq' => '&cire;',
     'clubsuit' => '&clubs;',
     'colon' => '&colon;',
     'Colon' => '&Colon;',
     'coloneq' => '&colone;',
     'complement' => '&comp;',
     'circ' => '&compfn;',
     'cong' => '&cong;',
     'oint' => '&conint;',
     'coprod' => '&coprod;',
     'cdots' => '&ctdot;',
     'curlyeqprec' => '&cuepr;',
     'curlyeqsucc' => '&cuesc;',
     'curvearrowleft' => '&cularr;',
     'cup' => '&cup;',
     'Cup' => '&Cup;',
     'curvearrowright' => '&curarr;',
     'curlyvee' => '&cuvee;',
     'curlywedge' => '&cuwed;',
     'dagger' => '&dagger;',
     'ddagger' => '&Dagger;',
     'daleth' => '&daleth;',
     'downarrow' => '&darr;',
     'Downarrow' => '&dArr;',
     'dashv' => '&dashv;',
     'downdownarrows' => '&ddarr;',
     'delta' => '&delta;',
     'Delta' => '&Delta;',
     'downharpoonleft' => '&dharl;',
     'downharpoonright' => '&dharr;',
     'diamond' => '&diam;',
     'diamondsuit' => '&diams;',
     'div' => '&divide;',
     'divideontimes' => '&divonx;',
     'ldots' => '&hellip;',
     'llcorner' => '&dlcorn;',
     'lrcorner' => '&drcorn;',
     'ddots' => '&dtdot;',
     'triangledown' => '&dtri;',
     'blacktriangledown' => '&dtrif;',
     'eqcirc' => '&ecir;',
     'eqcolon' => '&ecolon;',
     'ddotseq' => '&eDDot;',
     'doteqdot' => '&eDot;',
     'fallingdotseq' => '&efDot;',
     'eqslantgtr' => '&egs;',
     'ell' => '&ell;',
     'eqslantless' => '&els;',
     'varnothing' => '&emptyv;',
     'vdots' => '&vellip;',
     'straightepsilon' => '&epsi;',
     'varepsilon' => '&epsiv;',
     'questeq' => '&equest;',
     'equiv' => '&equiv;',
     'risingdotseq' => '&erDot;',
     'doteq' => '&esdot;',
     'esim' => '&esim;',
     'eta' => '&eta;',
     'exists' => '&exist;',
     'flat' => '&flat;',
     'forall' => '&forall;',
     'pitchfork' => '&fork;',
     'frown' => '&frown;',
     'gamma' => '&gamma;',
     'Gamma' => '&Gamma;',
     'digamma' => '&gammad;',
     'gtrapprox' => '&gap;',
     'ge' => '&ge;',
     'geq' => '&ge;',
     'geqq' => '&gE;',
     'gtreqless' => '&gel;',
     'gtreqqless' => '&gEl;',
     'geqslant' => '&ges;',
     'ggg' => '&Gg;',
     'gimel' => '&gimel;',
     'gtrless' => '&gl;',
     'gnapprox' => '&gnap;',
     'gneq' => '&gne;',
     'gneqq' => '&gnE;',
     'gnsim' => '&gnsim;',
     'gtrsim' => '&gsim;',
     'gg' => '&Gt;',
     'gtrdot' => '&gtdot;',
     'hbar' => '&hbar;',
     'leftrightarrow' => '&harr;',
     'Leftrightarrow' => '&hArr;',
     'leftrightsquigarrow' => '&harrw;',
     'Im' => '&image;',
     'imath' => '&imath;',
     'infty' => '&infin;',
     'int' => '&int;',
     'intercal' => '&intcal;',
     'iota' => '&iota;',
     'intprod' => '&iprod;',
     'in' => '&isin;',
     'kappa' => '&kappa;',
     'varkappa' => '&kappav;',
     'Lleftarrow' => '&lAarr;',
     'lambda' => '&lambda;',
     'Lambda' => '&Lambda;',
     'langle' => '&lang;',
     'lessapprox' => '&lap;',
     'leftarrow' => '&larr;',
     'Leftarrow' => '&lArr;',
     'Leftrightarrow' =>  '&Leftrightarrow;',
     'twoheadleftarrow' => '&Larr;',
     'hookleftarrow' => '&larrhk;',
     'looparrowleft' => '&larrlp;',
     'leftarrowtail' => '&larrtl;',
     'lceil' => '&lceil;',
     'lbrace' => '&lcub;',
     'le'  => '&le;',
     'leq' => '&le;',
     'leqq' => '&lE;',
     'lesseqgtr' => '&leg;',
     'lesseqqgtr' => '&lEg;',
     'leqslant' => '&les;',
     'lfloor' => '&lfloor;',
     'lessgtr' => '&lg;',
     'leftharpoondown' => '&lhard;',
     'leftharpoonup' => '&lharu;',
     'Ll' => '&Ll;',
     'leftleftarrows' => '&llarr;',
     'lmoustache' => '&lmoust;',
     'lnapprox' => '&lnap;',
     'lneq' => '&lne;',
     'lneqq' => '&lnE;',
     'lnsim' => '&lnsim;',
     'lozenge' => '&loz;',
     'blacklozenge' => '&lozf;',
     'leftrightarrows' => '&lrarr;',
     'leftrightharpoons' => '&lrhar;',
     'Lsh' => '&lsh;',
     'lesssim' => '&lsim;',
     'lbrack' => '&lsqb;',
     'll' => '&Lt;',
     'lessdot' => '&ltdot;',
     'leftthreetimes' => '&lthree;',
     'ltimes' => '&ltimes;',
     'triangleleft' => '&ltri;',
     'trianglelefteq' => '&ltrie;',
     'blacktriangleleft' => '&ltrif;',
     'maltese' => '&malt;',
     'mapsto' => '&map;',
     'mho' => '&mho;',
     'mid' => '&mid;',
     'ast' => '&midast;',
     'centerdot' => '&middot;',
     'boxminus' => '&minusb;',
     'dotminus' => '&minusd;',
     'mlcp' => '&mlcp;',
     'mp' => '&mnplus;',
     'models' => '&models;',
     'mu' => '&mu;',
     'multimap' => '&mumap;',
     'nabla' => '&nabla;',
     'napprox' => '&nap;',
     'natural' => '&natur;',
     'ncong' => '&ncong;',
     'ne' => '&ne;',
     'neq' => '&ne;',
     'nearrow' => '&nearr;',
     'nequiv' => '&nequiv;',
     'toea' => '&nesear;',
     'nexists' => '&nexist;',
     'ngeqq' => '&ngE;',
     'ngeqslant' => '&nges;',
     'ngtr' => '&ngt;',
     'nleftrightarrow' => '&nharr;',
     'nLeftrightarrow' => '&nhArr;',
     'ni' => '&ni;',
     'nleftarrow' => '&nlarr;',
     'nLeftarrow' => '&nlArr;',
     'nleqq' => '&nlE;',
     'nleqslant' => '&nles;',
     'nless' => '&nlt;',
     'ntriangleleft' => '&nltri;',
     'ntrianglelefteq' => '&nltrie;',
     'nmid' => '&nmid;',
     'neg' => '&not;',
     'notin' => '&notin;',
     'nparallel' => '&npar;',
     'nprec' => '&npr;',
     'nrightarrow' => '&nrarr;',
     'nRightarrow' => '&nrArr;',
     'ntriangleright' => '&nrtri;',
     'ntrianglerighteq' => '&nrtrie;',
     'nsucc' => '&nsc;',
     'nsim' => '&nsim;',
     'nsimeq' => '&nsime;',
     'nsubseteq' => '&nsube;',
     'nsubseteqq' => '&nsubE;',
     'nsupseteq' => '&nsupe;',
     'nsupseteqq' => '&nsupE;',
     'nu' => '&nu;',
     'nvdash' => '&nvdash;',
     'nvDash' => '&nvDash;',
     'nVdash' => '&nVdash;',
     'nVDash' => '&nVDash;',
     'nwarrow' => '&nwarr;',
     'circledast' => '&oast;',
     'circledcirc' => '&ocir;',
     'circleddash' => '&odash;',
     'odot' => '&odot;',
     'circlearrowleft' => '&olarr;',
     'omega' => '&omega;',
     'Omega' => '&Omega;',
     'ominus' => '&ominus;',
     'oplus' => '&oplus;',
     'vee' => '&or;',
     'circlearrowright' => '&orarr;',
     'circledS' => '&oS;',
     'oslash' => '&osol;',
     'otimes' => '&otimes;',
     'parallel' => '&par;',
     'partial' => '&part;',
     'perp' => '&perp;',
     'straightphi' => '&phi;',
     'Phi' => '&Phi;',
     'varphi' => '&phiv;',
     'pi' => '&pi;',
     'Pi' => '&Pi;',
     'varpi' => '&piv;',
     'hslash' => '&plankv;',
     'boxplus' => '&plusb;',
     'dotplus' => '&plusdo;',
     'pm' => '&plusmn;',
     'prec' => '&pr;',
     'precapprox' => '&prap;',
     'preccurlyeq' => '&prcue;',
     'preceq' => '&pre;',
     'prime' => '&prime;',
     'precnapprox' => '&prnap;',
     'precneqq' => '&prnE;',
     'precnsim' => '&prnsim;',
     'prod' => '&prod;',
     'propto' => '&prop;',
     'precsim' => '&prsim;',
     'psi' => '&psi;',
     'Psi' => '&Psi;',
     'iiiint' => '&qint;',
     'Rrightarrow' => '&rAarr;',
     'surd' => '&radic;',
     'rangle' => '&rang;',
     'rightarrow' => '&rarr;',
     'Rightarrow' => '&rArr;',
     'To' => '&rArr;',
     'twoheadrightarrow' => '&Rarr;',
     'hookrightarrow' => '&rarrhk;',
     'looparrowright' => '&rarrlp;',
     'rightarrowtail' => '&rarrtl;',
     'rightsquigarrow' => '&rarrw;',
     'ratio' => '&ratio;',
     'bkarow' => '&rbarr;',
     'dbkarow' => '&rBarr;',
     'drbkarow' => '&RBarr;',
     'dbar'     => '&dbar;',
     'dstrok'   => '&dstrok',
     'Dstrok'   => '&Dstrok',
     'rceil' => '&rceil;',
     'rbrace' => '&rcub;',
     'Re' => '&real;',
     'circledR' => '&reg;',
     'rfloor' => '&rfloor;',
     'rightharpoondown' => '&rhard;',
     'rightharpoonup' => '&rharu;',
     'rho' => '&rho;',
     'varrho' => '&rhov;',
     'rightleftarrows' => '&rlarr;',
     'rightleftharpoons' => '&rlhar;',
     'rmoustache' => '&rmoust;',
     'rightrightarrows' => '&rrarr;',
     'Rsh' => '&rsh;',
     'rbrack' => '&rsqb;',
     'rightthreetimes' => '&rthree;',
     'rtimes' => '&rtimes;',
     'triangleright' => '&rtri;',
     'trianglerighteq' => '&rtrie;',
     'blacktriangleright' => '&rtrif;',
     'succ' => '&sc;',
     'succapprox' => '&scap;',
     'succcurlyeq' => '&sccue;',
     'succeq' => '&sce;',
     'succnapprox' => '&scnap;',
     'succneqq' => '&scnE;',
     'succnsim' => '&scnsim;',
     'succsim' => '&scsim;',
     'cdot' => '&sdot;',
     'dotsquare' => '&sdotb;',
     'hksearow' => '&searhk;',
     'searrow' => '&searr;',
     'tosa' => '&seswar;',
     'setminus' => '&setmn;',
     'sharp' => '&sharp;',
     'sigma' => '&sigma;',
     'Sigma' => '&Sigma;',
     'varsigma' => '&sigmav;',
     'sim' => '&sim;',
     'thicksim' => '&thicksim;',
     'simeq' => '&sime;',
     'smile' => '&smile;',
     'spadesuit' => '&spades;',
     'sqcap' => '&sqcap;',
     'sqcup' => '&sqcup;',
     'sqsubset' => '&sqsub;',
     'sqsubseteq' => '&sqsube;',
     'sqsupset' => '&sqsup;',
     'sqsupseteq' => '&sqsupe;',
     'square' => '&square;',
     'blacksquare' => '&squarf;',
     'blacksquare' => '&squf;',
     'star' => '&sstarf;',
     'bigstar' => '&starf;',
     'subset' => '&sub;',
     'Subset' => '&Sub;',
     'subseteq' => '&sube;',
     'subseteqq' => '&subE;',
     'subsetneq' => '&subne;',
     'subsetneqq' => '&subnE;',
     'sum' => '&sum;',
     'supset' => '&sup;',
     'Supset' => '&Sup;',
     'supseteq' => '&supe;',
     'supseteqq' => '&supE;',
     'supsetneq' => '&supne;',
     'supsetneqq' => '&supnE;',
     'hkswarow' => '&swarhk;',
     'swarrow' => '&swarr;',
     'tau' => '&tau;',
     'therefore' => '&there4;',
     'theta' => '&theta;',
     'Theta' => '&Theta;',
     'to' => '&rarr;',
     'vartheta' => '&thetav;',
     'times' => '&times;',
     'boxtimes' => '&timesb;',
     'iiint' => '&tint;',
     'top' => '&top;',
     'triangleq' => '&trie;',
     'between' => '&twixt;',
     'uparrow' => '&uarr;',
     'Uparrow' => '&uArr;',
     'upharpoonleft' => '&uharl;',
     'upharpoonright' => '&uharr;',
     'ulcorner' => '&ulcorn;',
     'uplus' => '&uplus;',
     'upsilon' => '&upsi;',
     'Upsilon' => '&Upsi;',
     'urcorner' => '&urcorn;',
     'triangle' => '&utri;',
     'blacktriangle' => '&utrif;',
     'upuparrows' => '&uuarr;',
     'updownarrow' => '&varr;',
     'Updownarrow' => '&vArr;',
     'OverBrace'   => '&OverBrace;',
     'UnderBrace'   => '&UnderBrace;',
     'vdash' => '&vdash;',
     'vDash' => '&vDash;',
     'Vdash' => '&Vdash;',
     'veebar' => '&veebar;',
     'vert' => '&verbar;',
     'Vert' => '&Verbar;',
     'vartriangleleft' => '&vltri;',
     'nsubset' => '&vnsub;',
     'nsupset' => '&vnsup;',
     'varpropto' => '&vprop;',
     'vartriangleright' => '&vrtri;',
     'Vvdash' => '&Vvdash;',
     'wedgeq' => '&wedgeq;',
     'wp' => '&weierp;',
     'wr' => '&wreath;',
     'bigcap' => '&xcap;',
     'bigcirc' => '&xcirc;',
     'bigcup' => '&xcup;',
     'bigtriangledown' => '&xdtri;',
     'longleftrightarrow' => '&xharr;',
     'Longleftrightarrow' => '&xhArr;',
     'xi' => '&xi;',
     'Xi' => '&Xi;',
     'longleftarrow' => '&xlarr;',
     'Longleftarrow' => '&xlArr;',
     'longmapsto' => '&xmap;',
     'bigodot' => '&xodot;',
     'bigoplus' => '&xoplus;',
     'bigotimes' => '&xotime;',
     'longrightarrow' => '&xrarr;',
     'Longrightarrow' => '&xrArr;',
     'bigsqcup' => '&xsqcup;',
     'biguplus' => '&xuplus;',
     'bigtriangleup' => '&xutri;',
     'bigvee' => '&xvee;',
     'bigwedge' => '&xwedge;',
     'yen' => '&yen;',
     'zeta' => '&zeta;',
#      ',' => '&ThinSpace;',
#      ':' => '&MediumSpace;',
#      ';' => '&ThickSpace;',
#      '!' => '&NegativeThinSpace;',
     '~' => '&NonBreakingSpace;',
     'newline' => '&NewLine;',
     '\\' => '&NewLine;',
#      'quad' => '&Space;',
     'Kappa' => '&Kappa;',
     'Chi' => '&Chi;',
     'Eta' => '&Eta;',
     'Iota' => '&Iota;',
     'Mu' => '&Mu;',
     'Alpha' => '&Alpha;',
     'Nu' => '&Nu;',
     'Omicron' => '&Omicron;',
     'Epsilon' => '&Epsilon;',
     'Beta' => '&Beta;',
     'phi' => '&phi;',
     'omicron' => '&omicron;',
     'Zeta' => '&Zeta;',
     'Tau' => '&Tau;',
     'epsilon' => '&epsilon;',
     'Rho' => '&Rho;',
     'emptyset' => '&#x02205;',
     'land'     => '&and;',
     'lor'      => '&or;',
    );

my @function_names =
  (
   'arccos',
   'arcsin',
   'arctan',
   'arg',
   'cos',
   'cosh',
   'cot',
   'coth',
   'csc',
   'deg',
   'det',
   'diag',
   'dim',
   'exp',
   'gcd',
   'hom',
   'inf',
   'ker',
   'lg',
   'lim',
   'liminf',
   'limsup',
#   'lin',   - Both should create LIN as default output, see
#   'span',  - separate function definiton for details
   'ln',
   'log',
   'max',
   'min',
   'Pr',
   'sec',
   'sin',
   'sinh',
   'sup',
   'tan',
   'tanh',
  );

my @space_cmds
  = (
     "ThinSpace",
     "MediumSpace",
     "ThickSpace",
     "NegativeThinSpace",
     "NonBreakingSpace",
     "NewLine",
     "Space",
    );

my @greek_chars
  = (
     "Alpha",
     "alpha",
     "Beta",
     "beta",
     "Gamma",
     "gamma",
     "Delta",
     "delta",
     "Epsilon",
     "epsilon",
     "Zeta",
     "zeta",
     "Eta",
     "eta",
     "Theta",
     "theta",
     "Iota",
     "iota",
     "Kappa",
     "kappa",
     "Lambda",
     "lambda",
     "Mu",
     "mu",
     "Nu",
     "nu",
     "Xi",
     "xi",
     "Omicron",
     "omicron",
     "Pi",
     "pi",
     "Rho",
     "rho",
     "Sigma",
     "sigma",
     "Tau",
     "tau",
     "Upsilon",
     "upsilon",
     "Phi",
     "phi",
     "Chi",
     "chi",
     "Psi",
     "psi",
     "Omega",
     "omega",
    );

my @cmds_that_can_have_limits
  = (
     "sum",
#     "int",
     "oint",
     "prod",
     "coprod",
     "bigcap",
     "bigcup",
     "bigsqcup",
     "bigvee",
     "bigwedge",
     "bigodot",
     "bigotimes",
     "bigoplus",
     "biguplus",
     "iiint",
     "iiiint",
     @function_names,
    );

my @cmds_that_are_identifiers =
  (
   "aleph",
   "alpha",
   "beta",
   "beth",
   "bot",
   "backprime",
   "backslash",
   "checkmark",
   "chi",
   "clubsuit",
   "dagger",
   "ddagger",
   "daleth",
   "delta",
   "Delta",
   "diamond",
   "diamondsuit",
   "llcorner",
   "lrcorner",
   "ell",
   "varnothing",
   "straightepsilon",
   "varepsilon",
   "eta",
   "flat",
   "pitchfork",
   "gamma",
   "Gamma",
   "digamma",
   "gimel",
   "imath",
   "infty",
   "iota",
#   "iint",
   "kappa",
   "varkappa",
   "lambda",
   "Lambda",
   "maltese",
   "mho",
   "ast",
   "mu",
   "nabla",
   "natural",
   "ncong",
   "ntriangleleft",
   "nmid",
   "nu",
   "omega",
   "Omega",
   "straightphi",
   "Phi",
   "varphi",
   "pi",
   "Pi",
   "varpi",
   "hslash",
   "prime",
   "psi",
   "Psi",
   "rho",
   "varrho",
   "sharp",
   "sigma",
   "Sigma",
   "varsigma",
   "smile",
   "spadesuit",
   "star",
   "tau",
   "theta",
   "Theta",
   "vartheta",
   "upsilon",
   "Upsilon",
   "urcorner",
   "xi",
   "Xi",
   "yen",
   "zeta",
  );

my @left_right_delimiters =
  (
   '(',
   ')',
   '[',
   ']',
   '{',
   '}',
   '|',
   '/',
   '&DoubleVerticalBar;',
   '&lfloor;',
   '&lceil;',
   '&lang;',
   '&uarr;',
   '&darr;',
   '&varr;',
   '&rfloor;',
   '&rceil;',
   '&rang;',
   '&uArr;',
   '&dArr;',
   '&vArr;',
   '.',
  );

my %math_numbersets =
  (
   "Naturals"   => 'N',
   "Integers"   => 'Z',
   "Rationals"  => 'Q',
   "Reals"      => 'R',
   "Complex"    => 'C',
   "Field"      => 'K',
   "N"          => 'N',
   "I"          => 'I',
   "Q"          => 'Q',
   "R"          => 'R',
   "C"          => 'C',
   "Z"          => 'Z',
   "K"          => 'K',
   "M"          => 'M',
  );

# ------------------------------------------------------------------------------------------
# h2: Logging
# ------------------------------------------------------------------------------------------

#Ds
#a ()
# Writes additional parse log data. Intended to be the value of
# '$scan_proc->{additional_parse_logger}' in math mode.

sub log_math_parse_data
  {
    log_message("Math:\n");
    log_data
      (
       "Display", $scan_proc->{math_display},
       "Math mode", $scan_proc->{math_mode},
       "Paren mode", $scan_proc->{math_paren_mode},
       "Left paren", $scan_proc->{math_left_paren},
      );
  }

#Ds
#a ($node)
# Writes information concerning math node $node.

sub log_math_node
  {
    if ( $write_log )
      {
	my ($node, $indent, $prefix) = @_;
	( $indent ) || ( $indent = 0 );
	( $prefix ) || ( $prefix = "" );

	log_message
	  (
	   " " x $indent,
	   flush_string_right(($prefix? $prefix . " " : "") . $node->{type}, 30 - $indent),
	   flush_string_right($node->{source}->{scan_proc}, 20),
	   flush_string_right($node->{source}->{pos}, 10),
	   flush_string_right(($node->{left_paren}? $node->{left_paren} : ""), 3),
	   flush_string_right(($node->{right_paren}? $node->{right_paren} : ""), 3),
	   flush_string_right(($node->{scripts_as_limits}? $node->{scripts_as_limits} : ""), 3)
	  );

	if ( $node->{value} )
	  {
	    my $value_type = $scan_proc->{math_nodes}->{$node->{type}}->{value_type};
	    if ( $value_type eq "NODE" )
	      {
		log_message("\n");
		log_math_node($node->{value}, $indent+2);
	      }
	    elsif ( $value_type eq "NODE_LIST" )
	      {
		log_message("\n");
		log_math_node_list($node->{value}, $indent+2);
	      }
	    elsif ( $value_type eq "TEXT" )
	      {
		log_message(flush_string_right($node->{value}, 12), "\n");
	      }
	    elsif ( $value_type eq "OUTPUT_LIST" )
	      {
		log_message(flush_string_right($node->{value}, 12), "\n");
	      }
	    elsif ( $value_type eq "SPECIAL" )
	      {
		log_message(flush_string_right($node->{value}, 12), "\n");
	      }
	  }
	else
	  {
	    log_message(flush_string_right("-", 12), "\n");
	  }
	if ( $node->{subscript} )
	  {
	    log_math_node($node->{subscript}, $indent+2, "SUB");
	  }
	if ( $node->{superscript} )
	  {
	    log_math_node($node->{superscript}, $indent+2, "SUP");
	  }
      }
  }

#Ds
#a ($node_list, $indent)
# Logs all nodes in $node_list (a math node list). $node_list defaults to the current math
# node list. $indent is the number of spaces to be left on the left margin of the log
# messages; it defaults to 0.

sub log_math_node_list
  {
    if ( $write_log )
      {
	my ($node_list, $indent) = @_;
	$node_list ||= $scan_proc->{math_node_list};
	$indent ||= 0;

	for (my $count = 0; $count < scalar(@{$node_list}); $count++)
	  {
	    log_math_node($node_list->[$count], $indent);
	  }
      }
  }

#Ds
#a ()
# Logs the current math node list.

sub log_math_data
  {
    log_message
      (
       "\nMath data:\n",
       flush_string_right("Type", 30),
       flush_string_right("Source", 20),
       flush_string_right("Pos", 10),
       flush_string_right("L", 3),
       flush_string_right("R", 3),
       flush_string_right("UO", 3),
       flush_string_right("Value", 12),
       "\n",
       "-" x 80,
       "\n"
      );
    log_math_node_list();
    log_message
      (
       "-" x 80,
       "\n"
      );
  }

# ------------------------------------------------------------------------------------------
# h2: Creating MathML
# ------------------------------------------------------------------------------------------

#Ds
#a ()
# Switches to the MathML namespace, provided namespace output is enabled.

sub start_mathml_namespace
  {
    my $initialized = ( $_[0] || FALSE );
    if ( $set_mathml_namespace && $set_mathml_namespace_prefix )
      {
        new_xml_namespace($mathml_namespace, $mathml_namespace_prefix, $initialized);
      }
    elsif ( $set_mathml_namespace )
      {
        new_xml_namespace($mathml_namespace, undef(), $initialized);
      }
    elsif ( $set_mathml_namespace_prefix )
      {
        new_xml_namespace($mathml_namespace, $mathml_namespace_prefix, $initialized);
      }
  }

#Ds
#a ()
# Switches to the MathML extension namespace, provided namespace output is enabled.

sub start_mathml_ext_namespace
  {
    if ( ( $set_mathml_namespace ) && ( $set_mathml_namespace_prefix ) )
      {
        new_xml_namespace($mathml_ext_namespace, $mathml_ext_namespace_prefix, FALSE);
      }
    elsif ( $set_mathml_namespace )
      {
        new_xml_namespace($mathml_ext_namespace, undef(), FALSE);
      }
    elsif ( $set_mathml_namespace_prefix )
      {
        new_xml_namespace($mathml_ext_namespace, $mathml_ext_namespace_prefix, TRUE);
      }
  }

#Ds
#a ($name, $attribs, $layout, $auto_attribs_mode)
# Starts the MathML root element (i.e., the 'math' element).

sub start_mathml_root_element
  {
    my ($name, $attribs, @rest_args) = @_;
    log_message("\nstart_mathml_root_element\n");
    start_mathml_namespace();
    start_xml_element($name, $attribs, @rest_args);
  }

sub close_mathml_root_element
  {
    log_message("\nclose_mathml_root_element\n");
    close_mathml_element(@_);
    reset_xml_namespace();
  }

#Ds
#a ($value)
# Auxiliary function: If $value is a named entity like "&foo;", the function
# `resolve_xml_entity` (module "Serializer") is applied and the result returned;
# otherwise, $value is returned as-is.

sub resolve_mathml_value
  {
    my $value = $_[0];
    my $result;
    if ( $value =~ m/&([a-zA-Z0-9]+);/ )
      {
	my $entity = $1;
        $result = resolve_xml_entity($entity);
      }
    else
      {
        $result = $value;
      }
    return $result;
  }

sub start_mathml_element
  # ($name, @rest_args)
  {
    log_message("\nstart_mathml_element\n");
    my ($name, @rest_args) = @_;
    # $name = "$mathml_namespace_prefix:$name" if ( $set_mathml_namespace_prefix );
    start_xml_element($name, @rest_args);
  }

sub close_mathml_element
  # ($name, @rest_args)
  {
    log_message("\nclose_mathml_element\n");
    my ($name, @rest_args) = @_;
    # $name = "$mathml_namespace_prefix:$name" if ( $set_mathml_namespace_prefix );
    close_xml_element($name, @rest_args);
  }

sub empty_mathml_element
  # ($name, @rest_args)
  {
    log_message("\nempty_mathml_element\n");
    my ($name, @rest_args) = @_;
    # $name = "$mathml_namespace_prefix:$name" if ( $set_mathml_namespace_prefix );
    empty_xml_element($name, @rest_args);
  }

# ------------------------------------------------------------------------------------------
# h2: Creating math nodes
# ------------------------------------------------------------------------------------------

#Ds
#a ()
# Creates and returns a new math node. Only the "source" entry is initialized (with
# $scan_proc and `pos(${$scan_proc->{source}})`, respectively).

sub new_math_node
  {
    my $node = {};
    $node->{source} =
      {
       "scan_proc" => $scan_proc,
       "pos" => pos(${$scan_proc->{source}}),
      };
    return($node);
  }

sub release_math_node
  {
    my $node = $_[0];
    $node->{source}->{scan_proc} = undef();
#    delete($node->{source}->{scan_proc});
  }

sub release_math_node_list
  {
    my $node_list = $_[0];
    $node_list ||= $scan_proc->{math_node_list};
    foreach my $node (@{$node_list})
      {
        release_math_node_list($node);
      }
  }

sub append_math_node
  # ($node)
  {
    my $node = $_[0];

    if (not defined $node->{type})
      {
	notify_programming_error("Node with undefined type added");
      }

    if ( $scan_proc->{math_mode} eq "BASE" )
      {
	push(@{$scan_proc->{math_node_list}}, $node);
      }
    else
      {
	my $last_node = $scan_proc->{math_node_list}->[$#{$scan_proc->{math_node_list}}];
	if ( $scan_proc->{math_mode} eq "SUBSCRIPT" )
	  {
	    if ( ! $last_node->{subscript} )
	      {
		$last_node->{subscript} = $node;
	      }
	    else
	      {
		&{$scan_proc->{error_handler}}("Multiple subscripts for same base.");
	      }
	  }
	elsif ( $scan_proc->{math_mode} eq "SUPERSCRIPT" )
	  {
	    if ( ! $last_node->{superscript} )
	      {
		$last_node->{superscript} = $node;
	      }
	    else
	      {
		&{$scan_proc->{error_handler}}("Multiple superscripts for same base.");
	      }
	  }
      }
    $scan_proc->{prev_math_mode} =  $scan_proc->{math_mode};
    $scan_proc->{math_mode} = "BASE";
  }

sub get_last_math_node
  {
    my $real_last_node = '';
    my $last_node = $scan_proc->{math_node_list}->[$#{$scan_proc->{math_node_list}}];
    my $prev_mode = $scan_proc->{prev_math_mode};

    if ( !$prev_mode or $prev_mode eq "BASE" )
      {
	$real_last_node = pop(@{$scan_proc->{math_node_list}});
      }
    elsif ( $prev_mode eq "SUBSCRIPT" )
      {
	$real_last_node =  $last_node->{subscript};
	$last_node->{subscript} = undef();
      }
    elsif ( $prev_mode eq "SUPERSCRIPT" )
      {
	$real_last_node =  $last_node->{superscript};
	$last_node->{superscript} = undef();
      }
    else
      {
	notify_programming_error
	  ("get_last_math_node", "Unknown math mode: $prev_mode");
      }
    $scan_proc->{math_mode} = $prev_mode if ( $prev_mode );

    return $real_last_node;
  }

# ------------------------------------------------------------------------------------------
# h2: Math default attributes
# ------------------------------------------------------------------------------------------

sub init_default_math_attribs
  {
    log_message("\ninit_default_math_attribs\n");
    @default_math_attribs =
      (
       'class',
       'maxsize',
       'minsize',
       'align',
       'columnalign',
       'equation-no',
       'numalign',
       'stretchy',
       'largeop',
      );
  }

sub math_default_attribs
  {
    my ($node, $stage, $mode)  = @_;
    $stage ||= 'VALUE';
    $mode ||= 'TRANSFER';

    log_message("\nmath_default_attribs (1/2)\n");
    log_data("Node", $node, 'Stage', $stage, 'Mode', $mode);

    my $attribs = {};
    foreach my $key (@default_math_attribs)
      {
	if ( exists($node->{$key}) )
	  {
	    my ($value, $req_stage);
	    if ( ref($node->{$key}) )
	      {
		$value = $node->{$key}->{value};
		$req_stage = $node->{$key}->{stage};
	      }
	    else
	      {
		$value = $node->{$key};
	      }

            if ( !$value )
              {
                notify_programming_error
                  ("math_default_attribs", " Missing value for attribute: $key\n");
              }

	    log_message("\nmath_default_attribs (1.x/2)\n");
	    log_data('Attribute', $key, 'Value', $value, 'Stage', $req_stage);

	    if ( ( ! $req_stage ) || ( $req_stage eq $stage ) )
	      {
		$attribs->{$key} = $value;
		delete($node->{$key}) if ( $mode eq 'TRANSFER' );
	      }
	  }
      }

    log_message("\nmath_default_attribs (2/2)\n");
    return($attribs);
  }

sub append_to_math_node_class
  {
    my ($node, $to_add) = @_;

    log_message("\nappend_to_math_node_class 1/2 \n");
    log_data('Class', $to_add);

    if ( defined($node->{class}) && ref($node->{class}) eq "HASH" )
      {
	if ( defined($node->{class}->{value}) )
	  {
	    $node->{class}->{class} .= " " . $to_add;
	  }
	else
	  {
	    $node->{class}->{class} =  $to_add;
	  }
      }
    elsif( !ref($node->{class}) && defined($node->{class}) )
      {
	$node->{class} .= " " . $to_add;
      }
    else
      {
	$node->{class} = $to_add;
      }

    log_message("\nappend_to_math_node_class 2/2 \n");
    return $node;
}

# ------------------------------------------------------------------------------------------
# h2: Processing math nodes
# ------------------------------------------------------------------------------------------

sub handle_math_node
  # ($node)
  {
    my $node = $_[0];

    log_message("\nhandle_math_node 1/2\n");
    log_data("Node", $node,
	     "Type", $node->{type},
	     "Value", $node->{value});

    if ( ( $node->{subscript} ) && ( $node->{superscript} ) )
      {
	my $element = ( $node->{scripts_as_limits} ? "munderover" : "msubsup" );
	start_mathml_element($element, math_default_attribs($node, 'NODE'), "DISPLAY");
	&{$scan_proc->{math_nodes}->{$node->{type}}->{value_handler}}($node);
	handle_math_node($node->{subscript});
	handle_math_node($node->{superscript});
	close_mathml_element($element, "DISPLAY");
      }
    elsif ( $node->{subscript} )
      {
	my $element = ( $node->{scripts_as_limits} ? "munder" : "msub" );
	start_mathml_element($element, math_default_attribs($node, 'NODE'), "DISPLAY");
	&{$scan_proc->{math_nodes}->{$node->{type}}->{value_handler}}($node);
	handle_math_node($node->{subscript});
	close_mathml_element($element, "DISPLAY");
      }
    elsif ( $node->{superscript} )
      {
	my $element = ( $node->{scripts_as_limits} ? "mover" : "msup" );
	start_mathml_element($element, math_default_attribs($node, 'NODE'), "DISPLAY");
	&{$scan_proc->{math_nodes}->{$node->{type}}->{value_handler}}($node);
	handle_math_node($node->{superscript});
	close_mathml_element($element, "DISPLAY");
      }
    else
      {
	&{$scan_proc->{math_nodes}->{$node->{type}}->{value_handler}}($node);
      }

    release_math_node($node);

    log_message("\nhandle_math_node 2/2\n");
  }

sub handle_math_node_list
  # [$node_list]
  {
    log_message("\nhandle_math_node_list 1/2\n");

    my $node_list = $_[0];
    ( $node_list ) || ( $node_list = $scan_proc->{math_node_list} );
    for (my $count = 0; $count <= $#{$node_list}; $count++)
      {
	handle_math_node($node_list->[$count]);
      }

    log_message("\nhandle_math_node_list 2/2\n");
  }

# ==========================================================================================
# h1: Utilities
# ==========================================================================================

# ------------------------------------------------------------------------------------------
# h2: Math pseudo-environments
# ------------------------------------------------------------------------------------------

sub start_math_pseudo_env
  # ($pseudo_env_name)
  {
    my ($pseudo_env_name) = @_;

    log_message("\nstart_math_pseudo_env 1/2\n");
    log_data('Pseudo env', $pseudo_env_name);

    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{current_env} = $pseudo_env_name;
    $scan_proc->{math_mode} = "BASE";
    $scan_proc->{math_node_list} = [];

    log_message("\nstart_math_pseudo_env 2/2\n");
  }

sub close_math_pseudo_env
  # ($pseudo_env_name, $left_paren, $right_paren)
  {
    my ($pseudo_env_name, $left_paren, $right_paren) = @_;

    log_message("\nclose_math_pseudo_env 1/2\n");
    log_data("Pseudo env", $pseudo_env_name,
	     "Left paren", $left_paren,
	     "Right paren", $right_paren);

    if ( $scan_proc->{current_env} ne $pseudo_env_name )
      {
	&{$scan_proc->{error_handler}}
	  ("Improper nesting: ", appr_env_notation($scan_proc->{current_env}),
	   " must be closed before\n", appr_env_notation($pseudo_env_name),
	   " can be closed.");
      }

    my $math_node_list = $scan_proc->{math_node_list};

    reset_scan_proc();

    my $node = new_math_node();
    $node->{type} = "block";
    $node->{value} = $math_node_list;
    if ( $left_paren )
      {
        $node->{left_paren} = $left_paren;
      }
    if ( $right_paren )
      {
        $node->{right_paren} = $right_paren;
      }

    log_message("\nclose_math_pseudo_env 2/2\n");
    return $node;
  }

# ==========================================================================================
# h1: Initializing math mode
# ==========================================================================================

# --------------------------------------------------------------------------------
# h2: Starting math mode
# --------------------------------------------------------------------------------

sub start_math_mode
  #($math_display, $new_env, $own_scan_proc)
  # Function to switch to math mode. Used with all environments and commands creating
  # formulas.
  {
    my ($math_display, $new_env, $own_scan_proc) = @_;

    log_message("\nstart_math_mode 1/2\n");
    log_data('Display', $math_display, 'New env', $new_env, 'Own scan proc', $own_scan_proc);

    # Settings that differ in inline and block math.
    new_scan_proc("COPY", "OWN_NAMESPACE") if ( $own_scan_proc );
    $scan_proc->{math_display} = $math_display;
    $scan_proc->{current_env} = $new_env if ( $new_env );

    $scan_proc->{allowed_tokens} = [@math_token_types];
    $scan_proc->{math_nodes} ||= $default_math_nodes;
    $scan_proc->{par_enabled} = FALSE;
    $scan_proc->{mode} = "MATH";
    $scan_proc->{math_mode} = "BASE";
    $scan_proc->{math_paren_mode} = "BLOCK";
    $scan_proc->{math_node_list} = [];
    $scan_proc->{additional_parse_logger} = \&log_math_parse_data;

    disable_envs_from_all_libs('MATH_TOPLEVEL');
    disable_cmds_from_all_libs('MATH_TOPLEVEL');

    install_cmds_from_all_libs('MATH');
    install_envs_from_all_libs('MATH');

    $scan_proc->{locked_output_list_error_msg} =
      "Ouput locked because of 'math_mode' \n".
      "There should be no direct access to output list during math_mode \n".
      "Seems to be a programming error. \n";
    lock_output_list();

    log_message("\nstart_math_mode 2/2\n");
  }

sub end_math_mode
  {
    log_message("\nend_math_mode 1/2\n");
    unlock_output_list();
    log_message("\nend_math_mode 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# h2: Block math
# ------------------------------------------------------------------------------------------

sub start_display_math
  {
    log_message("\nstart_display_math\n");
    start_math_mode("BLOCK", undef, undef);
  }

sub close_display_math
  {
    log_message("\nclose_display_math 1/2\n");
    end_math_mode();
    log_message("\nclose_display_math 2/2\n");
  }

sub handle_display_math
  {
    log_message("\nhandle_display_math (1/2)\n");
    my $node_list = $_[0];
    ( $node_list ) || ( $node_list = $scan_proc->{math_node_list} );
    start_mathml_root_element('math', {"mode" => "display"}, "DISPLAY");
    handle_math_node_list($node_list);
    close_mathml_root_element('math', "DISPLAY");
    log_message("\nhandle_display_math (2/2)\n");
  }

sub begin_displaymath
  {
    log_message("\nbegin_displaymath\n");
    start_display_math();
    $scan_proc->{env_table}->{displaymath}->{begin_disabled} = TRUE;
  }

sub end_displaymath
  {
    log_message("\nclose_display_math\n");
    close_display_math();
    log_math_data();
    start_xml_element("displaymath", {}, "DISPLAY");
    handle_display_math();
    close_xml_element("displaymath", "DISPLAY");
  }

sub begin_equation
  {
    log_message("\nbegin_equation\n");
    start_display_math();
    $scan_proc->{env_table}->{equation}->{begin_disabled} = TRUE;
  }

sub end_equation
  {
    my $node_list = $scan_proc->{math_node_list};
    log_message("\nend_equation (1/2)\n");
    log_math_data();
    add_to_counter("equation", 1);

    end_math_mode();

    log_message("\nhandle_display_math_end_equation (1/2)\n");
    my $attribs = {"mode" => "display",
		   "equation-no" => get_counter_value("equation")};
    start_mathml_root_element('math', $attribs, "DISPLAY");
    handle_math_node_list($node_list);
    close_mathml_root_element('math', "DISPLAY");
    log_message("\nhandle_display_math_end_equation (2/2)\n");
    log_message("\nend_equation (2/2)\n");
  }

sub begin_equation_star
  {
    log_message("\nbegin_equation*\n");
    start_display_math();
    $scan_proc->{env_table}->{equation}->{begin_disabled} = TRUE;
  }

sub end_equation_star
  {
    my $node_list = $scan_proc->{math_node_list};
    log_message("\nend_equation* (1/2)\n");
    log_math_data();
    end_math_mode();
    handle_display_math();
    log_message("\nend_equation* (2/2)\n");
  }

# ------------------------------------------------------------------------------------------
# h2: Inline math
# ------------------------------------------------------------------------------------------

sub start_inline_math
  {
    run_hook($scan_proc->{start_inline_math_begin_hook});
    log_message("\nstart_inline_math\n");
    start_math_mode("INLINE", "_inline_math", "start_new_scanproc");
  }


sub close_inline_math
  {
    run_hook($scan_proc->{close_inline_math_begin_hook});
    log_message("\nclose_inline_math\n");
    run_hook($scan_proc->{close_inline_math_end_hook});
    reset_scan_proc();
  }

sub handle_inline_math
  {
    log_message("\nhandle_inline_math 1/2\n");
    my $node_list = $_[0];
    ( $node_list ) || ( $node_list = $scan_proc->{math_node_list} );
    start_mathml_root_element("math", {"mode" => "inline"}, "DISPLAY");
    handle_math_node_list($node_list);
    close_mathml_root_element("math", "DISPLAY");
    log_message("\nhandle_inline_math 2/2\n");
  }

sub handle_inline_math_boundry
  {
    log_message("\nhandle_inline_math_boundry\n");
    if ( $scan_proc->{mode} ne "MATH" )
      {
	start_inline_math();
      }
    else
      {
	if ( $scan_proc->{current_env} eq "_inline_math" )
	  {
	    log_math_data();
	    end_math_mode();
	    handle_inline_math();
	    close_inline_math();
	  }
	else
	  {
	    &{$scan_proc->{error_handler}}
	      (appr_env_notation($scan_proc->{current_env}),
	       " must be closed before inline math can be closed.");
	  }

      }
  }

# ==========================================================================================
# h1: Basic math constructs
# ==========================================================================================

# ------------------------------------------------------------------------------------------
# h2: Identifiers, numbers, operators, separators
# ------------------------------------------------------------------------------------------

sub handle_math_identifier_value
  {
    my $node = $_[0];
    my $value = $node->{value};
    log_message("\nhandle_math_identifier_value\n");
    log_data("Value", $value);
    start_mathml_element("mi", math_default_attribs($node), "SEMI_DISPLAY");
    xml_pcdata(resolve_mathml_value($value));
    close_mathml_element("mi", "INLINE");
  };

sub handle_math_number_value
  {
    my $node = $_[0];
    my $value = $node->{value};

    log_message("\nhandle_math_number_value\n  Value   :  $value\n");
    start_mathml_element("mn", math_default_attribs($node), "SEMI_DISPLAY");
    xml_pcdata($value);
    close_mathml_element("mn", "INLINE");
  };

sub handle_math_operator_value
  {
    my $node = $_[0];
    my $value = $node->{value};
    log_message("\nhandle_math_operator_value\n  Value   :  $value\n");
    start_mathml_element("mo", math_default_attribs($node), "SEMI_DISPLAY");
    xml_pcdata(resolve_mathml_value($value));
    close_mathml_element("mo", "INLINE");
  };

sub handle_math_separator_value
  {
    my $node = $_[0];
    my $value = $node->{value};
    log_message("\nhandle_math_separator_value\n  Value   :  $value\n");
    start_mathml_element("mo", math_default_attribs($node), "SEMI_DISPLAY");
    xml_pcdata($value);
    close_mathml_element("mo", "INLINE");
  };

# ------------------------------------------------------------------------------------------
# h2: Fractions and roots
# ------------------------------------------------------------------------------------------

#Ds
#a ($mode, $opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\frac' and similar commands.

sub execute_frac
  {
    my ($mode, $opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_frac (1/2)\n");
    log_data("Num align", $mode);

    my $numerator   = convert_math_as_block(\ ($man_args->[0]), $pos_man_args->[0]);
    my $denominator = convert_math_as_block(\ ($man_args->[1]), $pos_man_args->[1]);

    my $fraction =  new_math_node();
    $fraction->{type} = "fraction";
    $fraction->{value} = [$numerator, $denominator];
    $fraction->{numalign} = $mode if ($mode ne 'center');

    append_math_node($fraction);

    log_message("\nexecute_frac (2/2)\n");
  }

#Ds
#a ($node)
# Handler for values of math nodes of type "fraction".

sub handle_math_fraction_value
  {
    log_message("\nhandle_math_fraction_value (1/2)\n");
    my $node = $_[0];
    my $value = $node->{value};
    start_mathml_element("mfrac", math_default_attribs($node), "DISPLAY");
    handle_math_block_value($value->[0]); # numerator
    handle_math_block_value($value->[1]); # denominator
    close_mathml_element("mfrac", "DISPLAY");
    log_message("\nhandle_math_fraction_value (2/2)\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\sqrt' command.

sub execute_sqrt
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_root (1/2)\n");

    my $base = convert_math_as_block(\ ($man_args->[0]), $pos_man_args->[0]);

    my $index;
    if ( $opt_args->[0] )
      {
	$index = convert_math_as_block(\ ($opt_args->[0]), $pos_opt_args->[0]);
      }
    else
      {
	$index = FALSE;
      }

    my $root =  new_math_node();
    $root->{type} = "root";
    if ( $index )
      {
	$root->{value} = [$base, $index];
      }
    else
      {
	$root->{value} = [$base];
      }

    append_math_node($root);

    log_message("\nexecute_root (2/2)\n");
  }

#Ds
#a ($node)
# Handler for values of math nodes of type "root".

sub handle_math_root_value
  {
    log_message("\nhandle_math_root_value (1/2)\n");
    my $node = $_[0];
    my $value = $node->{value};
    if ( $value->[1] ) # if index is given
      {
	start_mathml_element("mroot", {}, "DISPLAY");
	handle_math_block_value($value->[0]); # base
	handle_math_block_value($value->[1]); # index
	close_mathml_element("mroot", "DISPLAY");
      }
    else
      {
	start_mathml_element("msqrt", {}, "DISPLAY");
	handle_math_block_value($value->[0]); # base
	close_mathml_element("msqrt", "DISPLAY");
      }
    log_message("\nhandle_math_root_value (2/2)\n");
  }

# --------------------------------------------------------------------------------
# h2: Spaces
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $value)
# Execute function of the '\hspace' command.

sub execute_math_hspace
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_math_hspace 1/3\n");

    my ($number, $unit) = get_length_from_arg(0, $man_args, $pos_man_args, 'CMD');
    my $value = $number . $unit;

    log_message("\nexecute_math_hspace 2/3\n");
    log_data("Value", $value);

    my $space = new_math_node();
    $space->{type} = 'hspace';
    $space->{value} = $value;

    append_math_node($space);
    log_message("\nexecute_math_hspace 3/3\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $value)
# Execute function of the math space cammnds '\quad', '\qqad', '\,', '\:', '\;', '\!'.
# The last argument, $value, is the size of the space (e.g., '1em').

sub execute_math_space_cmd
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $value) = @_;

    log_message("\nexecute_math_space_cmd\n");
    log_data("Value", $value);

    my $space = new_math_node();
    $space->{type} = 'hspace';
    $space->{value} = $value;

    append_math_node($space);
  }

#Ds
#a ($node)
# Handler for values of math nodes of type "hspace".

sub handle_math_hspace_value
  {
    my $node = $_[0];
    my $value = $node->{value};
    log_message("\nhandle_math_hspace_value\n");
    log_data("Value", $value);
    my $attribs = {'width' => $value};
    $attribs->{class} = $node->{class} if ( $node->{class} );
    empty_mathml_element('mspace', $attribs, 'INLINE');
  };

# ------------------------------------------------------------------------------------------
# h2: Blocks
# ------------------------------------------------------------------------------------------

#Ds
#a ($pseudo_env_name)
# Starts a math block. Opens a pseudo-environment with name $pseudo_env_name. A new scan
# process is started, too (which is the usual convention with pseudo-environments). The new
# scan process is given the math mode "BASE" and a new math node list, which is initially
# empty.

sub start_math_block
  # ($pseudo_env_name)
  {
    my $pseudo_env_name = $_[0];
    log_message("\nstart_math_block 1/2\n");
    log_data('Pseudo env', $pseudo_env_name);

    # Start new scan process:
    new_scan_proc("COPY", "SHARED_NAMESPACE");

    # Setup new scan process:
    $scan_proc->{current_env} = $pseudo_env_name;
    $scan_proc->{math_mode} = "BASE";
    $scan_proc->{math_node_list} = [];

    log_message("\nstart_math_block 2/2\n");
  }

#Ds
#a ($pseudo_env_name, $left_paren, $right_paren)
# Closes a math block. Expects that the current environment is pseudo_env_name, and signals
# an error if not. Creates a math node of type "block", resets the scan process (which
# closes the current environment, too), and appends the math node to the math node list.
# $left_paren and $right_paren are both optional. If $left_paren is set, the "left_paren"
# field of the new math node is set accordingly. Anologously, if $right_paren is set, the
# "right_paren" field of the new math node is set accordingly.

sub close_math_block
  # ($pseudo_env_name, $left_paren, $right_paren)
  {
    my ($pseudo_env_name, $left_paren, $right_paren) = @_;
    log_message("\nclose_math_block 1/2\n");
    log_data("Pseudo env", $pseudo_env_name,
	     "Left paren", $left_paren,
	     "Right paren", $right_paren);

    # Check nesting:
    if ( $scan_proc->{current_env} ne $pseudo_env_name )
      {
	&{$scan_proc->{error_handler}}
	  ("Improper nesting: ", appr_env_notation($scan_proc->{current_env}),
	   " must be closed before\n", appr_env_notation($pseudo_env_name),
	   " can be closed.");
      }

    # Create math node:
    my $node = new_math_node();
    $node->{type} = "block";
    $node->{value} = $scan_proc->{math_node_list};
    $node->{left_paren} = $left_paren if ( $left_paren );
    $node->{right_paren} = $right_paren if ( $right_paren );

    # Reset scan process:
    reset_scan_proc();

    # Append math node to list:
    append_math_node($node);

    log_message("\nclose_math_block 2/2\n");
  }

#Ds
#a ($node)
# Handler for values of math nodes of type "block".

sub handle_math_block_value
  {
    my $node = $_[0];
    my $value = $node->{value};
    my $left_paren = ( exists($node->{left_paren}) ? $node->{left_paren} : "" );
    my $right_paren = ( exists($node->{right_paren}) ? $node->{right_paren} : "" );
    my $attribs = math_default_attribs($node);
    my $size = scalar(@{$value});

    log_message("\nhandle_math_block_value 1/2\n");
    log_data("Left paren", $left_paren,
	     "Right paren", $right_paren,
	     "Size", $size,
	     "Paren mode", $mathml_paren_mode,
	     "Attribs", $attribs);

    if ( $mathml_paren_mode eq 'PLAIN' )
      {
	if ( ( $left_paren ) || ( $right_paren ) )
	  {
	    start_mathml_element('mrow', $attribs, 'DISPLAY');
	    start_mathml_element('mo', {}, 'SEMI_DISPLAY');
	    xml_pcdata(resolve_mathml_value($left_paren)) if ( $left_paren );
	    close_mathml_element('mo', 'INLINE');

	    handle_math_node_list($value);

	    start_mathml_element("mo", {}, "SEMI_DISPLAY");
	    xml_pcdata(resolve_mathml_value($right_paren)) if ( $right_paren );
	    close_mathml_element("mo", "INLINE");
	    close_mathml_element('mrow', 'DISPLAY');

	  }
	else
	  {
	    if ( $size > 1 )
	      {
		start_mathml_element('mrow', $attribs, 'DISPLAY');
		handle_math_node_list($value);
		close_mathml_element('mrow', 'DISPLAY');
	      }
	    else
	      {
		if (defined $attribs->{class})
		  {
		    $value->[0] = append_to_math_node_class($value->[0], $attribs->{class});
		  }
		handle_math_node_list($value);
	      }
	  }
      }
    elsif ( $mathml_paren_mode eq 'MFENCED' )
      {
	$attribs->{open} = resolve_mathml_value($left_paren);
	$attribs->{close} = resolve_mathml_value($right_paren);
	start_mathml_element('mfenced', $attribs, 'DISPLAY');
	handle_math_node_list($value);
	close_mathml_element('mfenced', 'DISPLAY');
      }

    log_message("\nhandle_math_block_value 2/2\n");
  }

# --------------------------------------------------------------------------------
# h2: Mathematical symbols
# --------------------------------------------------------------------------------

#Ds
#a ($cmd_name, $opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function for mathematical symbol commands.

sub execute_math_special_char_cmd
  {
    my ($cmd_name, $opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_math_special_char_cmd (1/2)\n");
    log_data("Command", $cmd_name);

    my $cmd = $scan_proc->{cmd_table}->{$cmd_name};
    my $node = new_math_node();

    $node->{type} = $cmd->{node_type};

    $node->{value} = $cmd->{char};
    $node->{scripts_as_limits} = TRUE if ( $cmd->{can_have_limits} );

    append_math_node($node);

    log_message("\nexecute_math_special_char_cmd (2/2)\n");
  }

# --------------------------------------------------------------------------------
# h2: Mathematical functions
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $func_name, $cmd_name)
# Execute function for commands that create the name of a mathematical function,
# e.g., '\sin', '\cos'. $func_name is the function name; it becomes the value of
# the respective math node. $cmd_name is the command name.

sub execute_math_function_name_cmd
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $func_name, $cmd_name) = @_;

    log_message("\nexecute_math_function_name_cmd\n");
    log_data("Function", $func_name, "Cmd", $cmd_name);

    my $cmd = $scan_proc->{cmd_table}->{$cmd_name};
    my $node = new_math_node();
    $node->{type} = 'identifier';
    $node->{value} = $func_name;
    $node->{scripts_as_limits} = TRUE if ( $cmd->{can_have_limits} );

    append_math_node($node);
  }

# --------------------------------------------------------------------------------
# h2: Mathematical accents
# --------------------------------------------------------------------------------

#Ds
#a ($node)
# Handler for values of math nodes of type "accented".

sub handle_math_accented_value
  {
    log_message("\nhandle_math_accented_value (1/2)\n");
    my $node = $_[0];
    my $value = $node->{value};
    my $attribs = math_default_attribs($node);
    $attribs->{accent} = 'true';
    start_mathml_element("mover", $attribs, "DISPLAY");
    handle_math_node($value->[0]);     # symbol
    handle_math_node($value->[1]);     # accent
    close_mathml_element("mover", "DISPLAY");
    log_message("\nhandle_math_accented_value (2/2)\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $accent_symbol)
# Execute function for accent commands ('\hat', '\bar', etc.). $accent_symbol
# is the symbol used as accent.

sub execute_math_accent_cmd
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $accent_symbol) = @_;

    log_message("\nexecute_math_accent_cmd (1/2)\n");
    log_data('Accent symbol', $accent_symbol),

    my $expression = convert_math_as_block(\($man_args->[0]), $pos_man_args->[0]);

    my $accent = new_math_node();
    $accent->{type} = "operator";
    $accent->{value} = $accent_symbol;

    my $accented =  new_math_node();
    $accented->{type} = "accented";
    $accented->{value} = [$expression, $accent];

    append_math_node($accented);

    log_message("\nexecute_math_accent_cmd (2/2)\n");
  }

# --------------------------------------------------------------------------------
# h2: Stacked expressions
# --------------------------------------------------------------------------------

#Ds
#a ($node)
# Handler for values of math nodes of type "overset".

sub handle_math_overset_value
  {
    log_message("\nhandle_math_overset_value (1/2)\n");
    my $node = $_[0];
    my $value = $node->{value};
    my $attribs = math_default_attribs($node);
    start_mathml_element("mover", $attribs, "DISPLAY");
    handle_math_node($value->[0]);     # expression
    handle_math_node($value->[1]);     # overline symbol
    close_mathml_element("mover", "DISPLAY");
    log_message("\nhandle_math_overset_value (2/2)\n");
  }

#Ds
#a ($node)
# Handler for values of math nodes of type "underset".

sub handle_math_underset_value
  {
    log_message("\nhandle_math_underset_value (1/2)\n");
    my $node = $_[0];
    my $value = $node->{value};
    my $attribs = math_default_attribs($node);
    start_mathml_element("munder", $attribs, "DISPLAY");
    handle_math_node($value->[0]);     # expression
    handle_math_node($value->[1]);     # underline symbol
    close_mathml_element("munder", "DISPLAY");
    log_message("\nhandle_math_underset_value (2/2)\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args,
#   $over_symbol, $base_symbol, $scripts_as_limits)
#
# Execute function for commands which place an expression (expr1) over another expression
# (expr2). The two expressions are obtained as follows:
#L
# - If both $over_symbol and $base_symbol are specified, expr1 and expr2 are set
#   to $over_symbol and $base_symbol, respectively. They are treated as single
#   operator nodes.
# - If $over_symbol is specified and $base_symbol is not specified, expr1 is
#   obtained as above, and expr2 is obtained from the first mandatory command
#   argument ('$man_args->[0]').
# - If $over_symbol is not specified and $base_symbol is specified, expr1 is
#   obtained from the first mandatory command argument ('$man_args->[0]'), and
#   expr2 is obtained as described in the first item of this list.
# - If both $over_symbol and $base_symbol are not specified, expr1 and expr2 are
#   obtained from the first and second mandatory command arguments, respectively
#   ('$man_args->[0]' and '$man_args->[1]', respectively).
#/L
# With this rules, commands having zero, one, or three arguments can be treated by this
# function, i.e commands of the forms:
#c
#  \foo{<over>}{<base>}
#  \bar{<base>}
#  \bazz
#/c
# where <over> is the TeX code for expr1, and <base> that of expr2.
#
# If $scripts_as_limits is specified, the "scripts_as_limits" flag of the
# overset math node is set to 'TRUE'.

sub execute_math_overset_cmd
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args,
        $over_symbol, $base_symbol, $scripts_as_limits) = @_;

    log_message("\nexecute_math_overset_cmd (1/2)\n");
    log_data('Over symbol', $over_symbol, 'Scrips as limits', $scripts_as_limits);

    my $i = 0;

    my $over;
    if ( $over_symbol )
      {
        $over = new_math_node();
        $over->{type} = "operator";
        $over->{value} = $over_symbol;
      }
    else
      {
        $over = convert_math_as_block(\($man_args->[$i]), $pos_man_args->[$i]);
        $i++;
      }

    my $base;
    if ( $base_symbol )
      {
        $base = new_math_node();
        $base->{type} = "operator";
        $base->{value} = $base_symbol;
      }
    else
      {
        $base = convert_math_as_block(\($man_args->[$i]), $pos_man_args->[$i]);
        $i++;
      }

    my $overset =  new_math_node();
    $overset->{type} = "overset";
    $overset->{value} = [$base, $over];
    $overset->{scripts_as_limits} = TRUE if ( $scripts_as_limits );

    append_math_node($overset);

    log_message("\nexecute_math_overset_cmd (2/2)\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args,
#   $under_symbol, $base_symbol, $scripts_as_limits)
#
# Execute function for commands which place an expression (expr1) under another expression
# (expr2). The two expressions are obtained as follows:
#L
# - If both $under_symbol and $base_symbol are specified, expr1 and expr2 are set
#   to $under_symbol and $base_symbol, respectively. They are treated as single
#   operator nodes.
# - If $under_symbol is specified and $base_symbol is not specified, expr1 is
#   obtained as above, and expr2 is obtained from the first mandatory command
#   argument ('$man_args->[0]').
# - If $under_symbol is not specified and $base_symbol is specified, expr1 is
#   obtained from the first mandatory command argument ('$man_args->[0]'), and
#   expr2 is obtained as described in the first item of this list.
# - If both $under_symbol and $base_symbol are not specified, expr1 and expr2 are
#   obtained from the first and second mandatory command arguments, respectively
#   ('$man_args->[0]' and '$man_args->[1]', respectively).
#/L
# With this rules, commands having zero, one, or three arguments can be treated by this
# function, i.e commands of the forms:
#c
#  \foo{<under>}{<base>}
#  \bar{<base>}
#  \bazz
#/c
# where <under> is the TeX code for expr1, and <base> that of expr2.
#
# If $scripts_as_limits is specified, the "scripts_as_limits" flag of the
# underset math node is set to 'TRUE'.

sub execute_math_underset_cmd
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args,
        $under_symbol, $base_symbol, $scripts_as_limits) = @_;

    log_message("\nexecute_math_underset_cmd (1/2)\n");
    log_data('Under symbol', $under_symbol, 'Scrips as limits', $scripts_as_limits);

    my $i = 0;

    my $under;
    if ( $under_symbol )
      {
        $under = new_math_node();
        $under->{type} = "operator";
        $under->{value} = $under_symbol;
      }
    else
      {
        $under = convert_math_as_block(\($man_args->[$i]), $pos_man_args->[$i]);
        $i++;
      }

    my $base;
    if ( $base_symbol )
      {
        $base = new_math_node();
        $base->{type} = "operator";
        $base->{value} = $base_symbol;
      }
    else
      {
        $base = convert_math_as_block(\($man_args->[$i]), $pos_man_args->[$i]);
        $i++;
      }

    my $underset =  new_math_node();
    $underset->{type} = "underset";
    $underset->{value} = [$base, $under];
    $underset->{scripts_as_limits} = TRUE if ( $scripts_as_limits );

    append_math_node($underset);

    log_message("\nexecute_math_underset_cmd (2/2)\n");
  }

# --------------------------------------------------------------------------------
# h2: \{ and \} command
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\{' command.

sub execute_curly_paren_left
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_curly_paren_left\n");
    log_data('Paren mode', $scan_proc->{math_paren_mode});

    if ( $scan_proc->{math_paren_mode} eq 'BLOCK' )
      {
	start_math_block('_math_{}_parenthesis');
      }
    elsif ( $scan_proc->{math_paren_mode} eq 'SINGLE' )
      {
	my $node = new_math_node();
	$node->{type} = "operator";
	$node->{value} = "{";
	append_math_node($node)
      }
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\}' command.

sub execute_curly_paren_right
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_curly_paren_right\n");
    log_data('Paren mode', $scan_proc->{math_paren_mode});

    if ( $scan_proc->{math_paren_mode} eq 'BLOCK' )
      {
	close_math_block('_math_{}_parenthesis', '{', '}');
      }
    elsif ( $scan_proc->{math_paren_mode} eq 'SINGLE' )
      {
	my $node = new_math_node();
	$node->{type} = "operator";
	$node->{value} = "}";
	append_math_node($node)
      }

  }

# --------------------------------------------------------------------------------
# h2: \left and \right command
# --------------------------------------------------------------------------------

#Ds
#a ($char)
# Returns the command that outputs the character $char, or 'FALSE' if no such
# command exists. $char may be the character itself or an entity specifying the
# character. This function is used to compose error messages.

sub get_math_cmd_for_char
  {
    my $char = $_[0];
    foreach my $cmd (keys(%cmds_vs_chars))
      {
	return($cmd) if ( $cmds_vs_chars{$cmd} eq $char );
      }
    return(FALSE);
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\left' command.

sub execute_left
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_left 1/3\n");

    # Scanning the delimiter
    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{math_node_list} = [];
    $scan_proc->{math_paren_mode} = 'SINGLE';
    Mumie::Scanner::scan_next_token();
    my $delimiter_node = $scan_proc->{math_node_list}->[0];
    reset_scan_proc();

    # Checking if a node was found
    if ( $delimiter_node )
      {
	# Checking if node has 'TEXT' value type
	if ( $scan_proc->{math_nodes}->{$delimiter_node->{type}}->{value_type} eq 'TEXT' )
	  {
	    # Checking if $delimiter is really a delimiter
	    my $delimiter = $delimiter_node->{value};
	    if ( grep($delimiter eq $_, @left_right_delimiters) )
	      {
		# Starting a math block, saving the delimiter
		start_math_block('_math_left_right');
		$scan_proc->{math_left_paren} = $delimiter;

		log_message("\nexecute_left 2/3\n");
		log_data("Delimiter", $delimiter);
	      }
	    else
	      {
		my $cmd = get_math_cmd_for_char($delimiter);
		&{$scan_proc->{error_handler}}
		  ("Invalid \\left delimiter: $delimiter", ($cmd ? " (\\$cmd)" : ''));
	      }
	  }
	else
	  {
	    &{$scan_proc->{error_handler}}
	      ("Command \\left must be followed by a delimiter");
	  }
      }
    else
      {
	&{$scan_proc->{error_handler}}("Missing delimiter");
      }

    log_message("\nexecute_left 3/3\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\right' command.

sub execute_right
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_right 1/3\n");

    # Scanning the delimiter
    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{math_node_list} = [];
    $scan_proc->{math_paren_mode} = 'SINGLE';
    Mumie::Scanner::scan_next_token();

    my $delimiter_node = $scan_proc->{math_node_list}->[0];
    reset_scan_proc();

    # Checking if a node was found
    if ( $delimiter_node )
      {
	# Checking if node has 'TEXT' value type
	if ( $scan_proc->{math_nodes}->{$delimiter_node->{type}}->{value_type} eq 'TEXT' )
	  {
	    # Checking if $delimiter is really a delimiter
	    my $right_delimiter = $delimiter_node->{value};
	    if ( grep($right_delimiter eq $_, @left_right_delimiters) )
	      {
		my $left_delimiter = $scan_proc->{math_left_paren};
		my $left_paren = ($left_delimiter eq '.' ? '' : $left_delimiter);
		my $right_paren = ($right_delimiter eq '.' ? '' : $right_delimiter);
		close_math_block('_math_left_right', $left_paren, $right_paren);
		log_message("\nexecute_right 2/3\n");
		log_data("Left delimiter", $left_delimiter,
			 "Right delimiter", $right_delimiter,
			 "Left paren", $left_paren,
			 "Right paren", $right_paren);
	      }
	    else
	      {
		my $cmd = get_math_cmd_for_char($right_delimiter);
		&{$scan_proc->{error_handler}}
		  ("Invalid \\right delimiter: $right_delimiter", ($cmd ? " (\\$cmd)" : ''));
	      }
	  }
	else
	  {
	    &{$scan_proc->{error_handler}}
	      ("Command \\right must be followed by a delimiter");
	  }
      }
    else
      {
	&{$scan_proc->{error_handler}}("Missing delimiter");
      }

    log_message("\nexecute_right 3/3\n");
  }

# --------------------------------------------------------------------------------
# h2: \limits and \nolimits commands
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\limis' command.

sub execute_limits
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_limits\n");
    my $last_node = $scan_proc->{math_node_list}->[$#{$scan_proc->{math_node_list}}];
    $last_node->{scripts_as_limits} = TRUE;
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\nolimis' command.

sub execute_nolimits
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_limits\n");
    my $last_node = $scan_proc->{math_node_list}->[$#{$scan_proc->{math_node_list}}];
    $last_node->{scripts_as_limits} = FALSE;
  }

# --------------------------------------------------------------------------------
# h2: Calligraphic letters (\cal command)
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\cal' command.

sub execute_cal
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_cal (1/2)\n");

    my $text = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    if ( $text !~ m/^[a-zA-Z]+$/ )
      {
        &{$scan_proc->{error_handler}}
          ("Invalid \\cal argument: $text (must contain only letters)");
      }

    foreach my $char (split(//, $text))
      {
        my $node = new_math_node();
        $node->{type} = 'identifier';
        $node->{value} = '&' . $char . 'scr;';
        append_math_node($node);
      }

    log_message('execute_cal 2/2');
  }

# --------------------------------------------------------------------------------
# h2: Normal text in math mode
# --------------------------------------------------------------------------------

#Ds
#a ()
# Auxiliary function. Returns the last scan process the mode of which is not
# "MATH". If no such scan process exists, returns 'undef()'.

sub get_last_nonmath_scan_proc
  {
    my @scan_proc_list = @Mumie::Scanner::scan_proc_list;
    my $depth = $#scan_proc_list;
    while ( $depth >= 0 && $scan_proc_list[$depth]->{mode} eq 'MATH' )
      {
        $depth--;
      }
    return($depth >= 0 ? $scan_proc_list[$depth] : undef());
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function for the '\text' and '\mbox' commands.

sub execute_math_normal_text
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_math_normal_text 1/2\n");

    my $pre_hook = sub
      {
        # Get last non-math scan process:
        my $last_nonmath_scan_proc = get_last_nonmath_scan_proc();

        # Copy relevant fields:
        if ( $last_nonmath_scan_proc )
          {
            my @fields_to_copy =
              ('allowed_tokens',
               'mode',
               'token_table',
               'cmd_table',
               'env_table');

            foreach my $field (@fields_to_copy)
              {
                $scan_proc->{$field} = copy_value($last_nonmath_scan_proc->{$field})
                  if ( defined($last_nonmath_scan_proc) );
              }
          }

        # Set environment name:
        $scan_proc->{current_env} = "_math_normal_text";

        # Start non-math element:
        start_xml_element('non-math', {}, 'DISPLAY');
      };

    my $post_hook = sub
      {
        # Close non-math element:
        close_xml_element('non-math', 'DISPLAY');
      };

    # Convert argument:
    my $output_list =
      convert_subexp(\($man_args->[0]), $pos_man_args->[0], $pre_hook, $post_hook);

    my $node = new_math_node();
    $node->{type} = "normal_text";
    $node->{value} = $output_list;

    append_math_node($node);
    log_message("\nexecute_math_normal_text 2/2\n");
  }

sub handle_math_normal_text_value
  {
    log_message("\nhandle_math_normal_text_value\n");
    my $node = $_[0];
    my $value = $node->{value};
    reset_xml_namespace();
    push(@{$scan_proc->{output_list}}, @{$value});
    start_mathml_namespace(TRUE);
  };

# ==========================================================================================
# h1: Extended math constructs
# ==========================================================================================

# --------------------------------------------------------------------------------
# h2: Binomial coefficients
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\binom' command.

sub execute_binom
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_binom (1/2)\n");

    my $top = convert_math_as_block(\($man_args->[0]), $pos_man_args->[0]);
    my $bottom = convert_math_as_block(\($man_args->[1]), $pos_man_args->[1]);

    my $binom_coeff =  new_math_node();
    $binom_coeff->{type} = "binom_coeff";
    $binom_coeff->{value} = [$top, $bottom];

    append_math_node($binom_coeff);

    log_message("\nexecute_binom (2/2)\n");
  }

#Ds
#a ($node)
# Handler for values of math nodes of type "binom_coeff".

sub handle_math_binom_coeff_value
  {
    log_message("\nhandle_math_binom_coeff_value (1/2)\n");
    my $node = $_[0];
    my $value = $node->{value};

    # Start enclosing mrow:
    start_mathml_element("mrow", {}, "DISPLAY");

    # Left parenthesis:
    start_mathml_element('mo', {}, 'SEMI_DISPLAY');
    xml_pcdata('(');
    close_mathml_element("mo", 'INLINE');

    # The two expressions:
    start_mathml_element("mfrac", {linethickness => "0"}, "DISPLAY" );
    handle_math_block_value($value->[0]);
    handle_math_block_value($value->[1]);
    close_mathml_element("mfrac", "DISPLAY");

    # Right parenthesis:
    start_mathml_element('mo', {}, 'SEMI_DISPLAY');
    xml_pcdata(')');
    close_mathml_element("mo", 'INLINE');

    # Close enclosing mrow:
    close_mathml_element("mrow", "DISPLAY");

    log_message("\nhandle_math_binom_coeff_value (2/2)\n");
  }

# --------------------------------------------------------------------------------
# h2: Logical relation symbols (\implies, \longimplies, etc.)
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\implies' command.

sub execute_implies
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_implies (1/2)\n");

    my $arrow_type = $opt_args->[0] || "r";
    my $arrow_type_vs_cmd =
      {
       r => "Rightarrow",
       l => "Leftarrow",
       t => "Uparrow",
       u => "Uparrow",
       d => "Downarrow",
       b => "Downarrow",
      };

    if ( not exists($arrow_type_vs_cmd->{$arrow_type}) )
      {
	&{$scan_proc->{error_handler}}
          ("Invalid arrow type specifier: $arrow_type");
      }

    my $cmd = $arrow_type_vs_cmd->{$arrow_type};
    my $char = $cmds_vs_chars{$cmd};

    my $implies = new_math_node();
    $implies->{type} = "operator";
    $implies->{value} = $char;

    append_math_node($implies);

    log_message("\nexecute_implies (2/2)\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\longimplies' command.

sub execute_longimplies
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_longimplies (1/2)\n");

    my $arrow_type = $opt_args->[0] || "r";
    my $arrow_type_vs_cmd =
      {
       r => "Longrightarrow",
       l => "Longleftarrow",
       t => "Uparrow",
       u => "Uparrow",
       d => "Downarrow",
       b => "Downarrow",
      };

    if ( not exists($arrow_type_vs_cmd->{$arrow_type}) )
      {
	&{$scan_proc->{error_handler}}
          ("Invalid arrow type specifier: $arrow_type");
      }

    my $cmd = $arrow_type_vs_cmd->{$arrow_type};
    my $char = $cmds_vs_chars{$cmd};

    my $longimplies = new_math_node();
    $longimplies->{type} = "operator";
    $longimplies->{value} = $char;

    #####--------------------#####
    #        WORKAROUND          #
    #        for missing         #
    #      Longupdownarrow       #
    #####--------------------#####
    if ($arrow_type =~ /t|u|d|b/)
      {
	$longimplies->{minsize}="2em";
      }

    append_math_node($longimplies);

    log_message("\nexecute_longimplies (2/2)\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\iff' command.

sub execute_iff
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_iff (double arrow) (1/2)\n");

    my $arrow_type = $opt_args->[0] || "r";
    my $arrow_type_vs_cmd =
      {
       r => "Leftrightarrow",
       l => "Leftrightarrow",
       h => "Leftrightarrow",
       v => "Updownarrow",
       u => "Updownarrow",
       d => "Updownarrow",
      };

    if ( not exists($arrow_type_vs_cmd->{$arrow_type}) )
      {
	&{$scan_proc->{error_handler}}
          ("Invalid arrow type specifier: $arrow_type");
      }

    my $cmd = $arrow_type_vs_cmd->{$arrow_type};
    my $char = $cmds_vs_chars{$cmd};

    my $implies = new_math_node();
    $implies->{type} = "operator";
    $implies->{value} = $char;

    append_math_node($implies);

    log_message("\nexecute_iff (2/2)\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\longiff' command.

sub execute_longiff
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_longiff (double arrow) (1/2)\n");

    my $arrow_type = $opt_args->[0] || "r";
    my $arrow_type_vs_cmd =
      {
       r => "Longleftrightarrow",
       l => "Longleftrightarrow",
       h => "Longleftrightarrow",
       v => "Updownarrow",
       u => "Updownarrow",
       d => "Updownarrow",
      };

    if ( not exists($arrow_type_vs_cmd->{$arrow_type}) )
      {
	&{$scan_proc->{error_handler}}
          ("Invalid arrow type specifier: $arrow_type");
      }

    my $cmd = $arrow_type_vs_cmd->{$arrow_type};
    my $char = $cmds_vs_chars{$cmd};

    my $implies = new_math_node();
    $implies->{type} = "operator";
    $implies->{value} = $char;

    #####--------------------#####
    #        WORKAROUND          #
    #        for missing         #
    #      Longupdownarrow       #
    #####--------------------#####
    if ($arrow_type =~ /v|u|d/)
      {
	$implies->{minsize}="2em";
      }


    append_math_node($implies);

    log_message("\nexecute_longiff (2/2)\n");
  }

# --------------------------------------------------------------------------------
# h2: Absolute value (\abs command)
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\longiff' command.

sub execute_abs
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_abs (1/2)\n");

    my $content = convert_math_as_block(\ ($man_args->[0]), $pos_man_args->[0]);

    $content->{left_paren} = "|";
    $content->{right_paren} = "|";

    append_math_node($content);

    log_message("\nexecute_abs (2/2)\n");
  }

# --------------------------------------------------------------------------------
# h2: \arrowvert and \Arrowvert commands
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\arrowvert' command.

sub execute_arrowvert
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $node = new_math_node();

    log_message("\nexecute_arrowvert (1/1)\n");

    $node->{type} = "operator";
    $node->{value} = "|";
    $node->{minsize}="1.2em";
    append_math_node($node);
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\Arrowvert' command.

sub execute_Arrowvert
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $node = new_math_node();

    log_message("\nexecute_arrowvert (1/1)\n");

    $node->{type} = "operator";
    $node->{value} = "&DoubleVerticalBar;";
    $node->{minsize}="1.2em";
    append_math_node($node);
  }

# --------------------------------------------------------------------------------
# h2: Vectors (\vec, \vecstyle, \vector commands)
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\vec' command.

sub execute_vec
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_vec 1/2\n");

    my $node_list = convert_math_subexp(\ ($man_args->[0]), $pos_man_args->[0]);
    my $size = scalar(@{$node_list});
    my $style = $scan_proc->{vec_style};

    if ( $size == 1 )
      {
	my $vec = $node_list->[0];
	$vec->{class} = 'vector';
	$vec->{class} .= " $style" if $style;
	if (not ($vec->{subscript} || $vec->{superscript}))
	  {
	    $vec->{class} = {'value' => $vec->{class}, 'stage' => 'VALUE'};
	  }
	append_math_node($vec);
      }
    elsif ( $size > 1 )
      {
	my $vec = new_math_node();
	$vec->{type} = 'block';
	$vec->{class} = 'vector';
	$vec->{class} .= " $style" if $style;
	if (not ($vec->{subscript} || $vec->{superscript}))
	  {
	    $vec->{class} = {'value' => $vec->{class}, 'stage' => 'VALUE'};
	  }
	$vec->{value} = $node_list;
	append_math_node($vec);
      }

    log_message("\nexecute_vec 2/2\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\vecstyle' command.

sub execute_vecstyle
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_vecstyle 1/2\n");

    my $style = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');

    my @allowed_styles =
      (
       'arrow',
       'bold',
       'underline',
       'frakt',
       'plain',
       'double-struck',
       'italic',
       'bold-italic',
       'monospace',
       'bold-frakt',
       'script',
       'sans-serif',
      );

    if ( !grep($_ eq $style, @allowed_styles) )
      {
	&{$scan_proc->{error_handler}}("Invalid vector style: $style");
      }
	
    log_data("Style", $style);
    $scan_proc->{vec_style} = $style;

    log_message("\nexecute_vecstyle 2/2\n");
    log_data('Style', $style);
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\vector' command.

sub execute_vector
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_vector 1/3\n");

    my $style = 
      ($opt_args->[0] ? get_data_from_arg(0, $opt_args, $pos_opt_args, 'CMD') : 'c');


    if ( ! grep($style eq $_, 'r', 'c') )
      {
	&{$scan_proc->{error_handler}}("Invalid vector notation style: $style");
      }

    log_message("\nexecute_vector 2/3\n");
    log_data('Style', (($style eq 'c')? 'Col' : 'Row'));

    my $pre_scan_hook = sub
      {
        # Initialize vector component list:
        $scan_proc->{vector_nodes} = [];

        # If necessary, define the token type for vector component separartors (//):
        unless ( $scan_proc->{token_table}->{vector_seperator} )
          {
            $scan_proc->{token_table}->{vector_seperator} =
              {
               'tester' => sub { test_regexp("\\\\\\\\") },
               'handler' => sub
                 {
                   # Add last vector component to list:
                   my $vec_comp = new_math_node();
                   $vec_comp->{type} = 'block';
                   $vec_comp->{value} = $scan_proc->{math_node_list};
                   push(@{$scan_proc->{vector_nodes}}, $vec_comp);

                   # Clear math node list for next vector component:
                   $scan_proc->{math_node_list} = [];
                 },
              };
          }

        # Enable above token type:
	$scan_proc->{allowed_tokens} = ["vector_seperator", @{$scan_proc->{allowed_tokens}}];
      };

    my $post_scan_hook = sub
      {
        # Add last vector component to list:
	my $vec_comp = new_math_node();
	$vec_comp->{type} = 'block';
	$vec_comp->{value} = $scan_proc->{math_node_list};
	push(@{$scan_proc->{vector_nodes}}, $vec_comp);

        # Set math node list to vector component list:
	$scan_proc->{math_node_list} = $scan_proc->{vector_nodes};
      };

    my $node_list = convert_math_subexp
      (\($man_args->[0]), $pos_man_args->[0], $pre_scan_hook, $post_scan_hook);

    my $size = scalar(@{$node_list});

    if ( $size == 1 )
      {
	my $vec = $node_list->[0];
	$vec->{class} = 'row-vector' if $style eq "r";
	$vec->{class} = 'column-vector' if $style eq "c";
	append_math_node($vec);
      }
    elsif ( $size > 1 )
      {
	my $vec = new_math_node();
	$vec->{type} = 'block';
	$vec->{class} = 'row-vector' if $style eq "r";
	$vec->{class} = 'column-vector' if $style eq "c";
	$vec->{value} = $node_list;
	append_math_node($vec);
      }

    log_message("\nexecute_vector 2/2\n");
  }

# --------------------------------------------------------------------------------
# h2: Norm (\norm and \norm* command)
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\norm' command.

sub execute_norm
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_norm (1/2)\n");
    my $norm =  new_math_node();
    $norm->{type} = "norm";
    $norm->{value} = convert_math_as_block(\($man_args->[0]), $pos_man_args->[0]);
    $norm->{class} = 'norm-' . get_data_from_arg(0, $opt_args, $pos_opt_args)
      if ( $opt_args->[0] );
    append_math_node($norm);
    log_message("\nexecute_norm (2/2)\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\norm*' command.

sub execute_norm_star
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_norm* (1/2)\n");
    my $norm =  new_math_node();
    $norm->{type} = "norm";
    $norm->{value} = convert_math_as_block(\($man_args->[0]), $pos_man_args->[0]);
    $norm->{class} = 'norm-R';
    append_math_node($norm);
    log_message("\nexecute_norm* (2/2)\n");
  }

#Ds
#a ($node)
# Handler for values of math nodes of type "norm".

sub handle_math_norm_value
  {
    log_message("\nhandle_math_norm_value (1/2)\n");

    my $node = $_[0];
    my $value = $node->{value};

    # Start "norm" element:
    start_mathml_ext_namespace();
    start_xml_element("norm", math_default_attribs($node), "DISPLAY");

    # Handle vector:
    start_mathml_namespace();
    handle_math_block_value($value);
    reset_xml_namespace();

    # Close "norm" element:
    close_xml_element("norm", "DISPLAY");
    reset_xml_namespace();

    log_message("\nhandle_math_norm_value (2/2)\n");
  }

# --------------------------------------------------------------------------------
# h2: Inner products (\inner command)
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\inner' command.

sub execute_inner
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_inner (1/2)\n");

    my $vec1 = convert_math_as_block(\($man_args->[0]), $pos_man_args->[0]);
    my $vec2 = convert_math_as_block(\($man_args->[1]), $pos_man_args->[1]);

    my $inner_product = new_math_node();
    $inner_product->{type} = "inner_product";
    $inner_product->{value} = [$vec1, $vec2];

    append_math_node($inner_product);

    log_message("\nexecute_inner (2/2)\n");
  }

#Ds
#a ($node)
# Handler for values of math nodes of type "inner_product".

sub handle_math_inner_product_value
  {
    log_message("\nhandle_math_inner_product_value (1/2)\n");

    my $node = $_[0];
    my $value = $node->{value};

    # Start "innerproduct" element:
    start_mathml_ext_namespace();
    start_xml_element("innerproduct", math_default_attribs($node), "DISPLAY");

    # Handle first argument vector:
    start_mathml_namespace();
    handle_math_block_value($value->[0]);
    reset_xml_namespace();

    # Handle second argument vector:
    start_mathml_namespace();
    handle_math_block_value($value->[1]);
    reset_xml_namespace();

    # Close "innerproduct" element:
    close_xml_element("innerproduct", "DISPLAY");
    reset_xml_namespace();

    log_message("\nhandle_math_inner_product_value (2/2)\n");
  }

# --------------------------------------------------------------------------------
# h2: Identical map (\id command)
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\id' command.

sub execute_math_id
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_math_id (1/2)\n");
    my $idmap =  new_math_node();
    $idmap->{type} = "idmap";
    $idmap->{value} =
      ($opt_args->[0]
       ? convert_math_as_block(\($opt_args->[0]), $pos_opt_args->[0])
       : undef ());
    append_math_node($idmap);
    log_message("\nexecute_math_id (2/2)\n");
  }

#Ds
#a ($node)
# Handler for values of math nodes of type "idmap".

sub handle_math_idmap_value
  {
    log_message("\nhandle_math_idmap_value (1/2)\n");
    my $node = $_[0];

    if ( $node->{value} )
      {
        # Start "idmap" element:
        start_mathml_ext_namespace();
        start_xml_element("idmap", math_default_attribs($node), "DISPLAY");

        # Handle value:
        start_mathml_namespace();
        handle_math_block_value($node->{value});
        reset_xml_namespace();

        # Close "idmap" element:
        close_xml_element("idmap", "DISPLAY");
        reset_xml_namespace();
      }
    else
      {
        start_mathml_ext_namespace();
        empty_xml_element("idmap", math_default_attribs($node), "DISPLAY");
        reset_xml_namespace();
      }

    log_message("\nhandle_math_idmap_value (2/2)\n");
  }

# --------------------------------------------------------------------------------
# h2: Complex numbers (\cnum command)
# --------------------------------------------------------------------------------

sub execute_cnum
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_cnum (1/2)\n");

    # Get real part:
    my $re_part;
    if ( $man_args->[0] =~ m/^\s*$/ )
      {
        # Argument left blank - create empty number node:
	my $re_part_num = new_math_node();
	$re_part_num->{type} = "number";
	$re_part_num->{value} = '';
	$re_part = new_math_node();
	$re_part->{type} = "block";
	$re_part->{value} = [$re_part_num];
      }
    else
      {
        $re_part = convert_math_as_block(\ ($man_args->[0]), $pos_man_args->[0]);
      }

    # Get imaginary part:
    my $im_part;
    if ( $man_args->[1] =~ m/^\s*$/ )
      {
        # Argument left blank - create empty number node:
	my $im_part_num = new_math_node();
	$im_part_num->{type} = "number";
	$im_part_num->{value} = '';
	$im_part = new_math_node();
	$im_part->{type} = "block";
	$im_part->{value} = [$im_part_num];
      }
    else
      {
        $im_part = convert_math_as_block(\ ($man_args->[1]), $pos_man_args->[1]);
      }

    my $complex_number =  new_math_node();
    $complex_number->{type} = "cnum";
    $complex_number->{value} = [$re_part, $im_part];
    append_math_node($complex_number);

    log_message("\nexecute_cnum (2/2)\n");
  }

#Ds
#a ($node)
# Handler for values of math nodes of type "cnum".

sub handle_math_cnum_value
  {
    log_message("\nhandle_math_cnum_value (1/2)\n");

    my $node = $_[0];
    my $value = $node->{value};

    # Start "cnum" element:
    start_mathml_ext_namespace();
    start_xml_element("cnum", math_default_attribs($node), "DISPLAY");

    # Handle real part:
    start_mathml_namespace();
    handle_math_block_value($value->[0]);
    reset_xml_namespace();

    # Handle imaginary part:
    start_mathml_namespace();
    handle_math_block_value($value->[1]);
    reset_xml_namespace();

    # Close "cnum" element:
    close_xml_element("cnum", "DISPLAY");
    reset_xml_namespace();

    log_message("\nhandle_math_cnum_value (2/2)\n");
  }

# --------------------------------------------------------------------------------
# h2: Integral signs (\int, \iint, etc., \idotsint)
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $num)
# Execute function for commands that produce one or more integral signs ('\int',
# '\iint', '\iiiint', etc.).

sub execute_int
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $num) = @_;
    log_message("\nexecute_int (1/2)\n");
    log_data('Num', $num);

    my @nodes = ();
    my $node = new_math_node();
    $node->{type}    ='operator';
    $node->{'value'} ='&int;';
    $node->{'stretchy'} ='true';
    $node->{'largeop'} ='true';

    for (my $count = 0; $count <= $num; $count++)
      {
	push(@nodes, $node);
      }

    my $res = new_math_node();
    $res->{type} = 'block';
    $res->{value} = \@nodes;
    append_math_node($res);

    log_message("\nexecute_int (2/2)\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function for the '\idotsint' command.

sub execute_idotsint
  {
    my ($mode, $base, $arg) = @_;
    log_message("\nexecute_idotsint\n");
    my $int = new_math_node();
    $int->{type} = "identifier";
    $int->{value} = "&int;";
    my $dots = new_math_node();
    $dots->{type} = "identifier";
    $dots->{value} = "&ctdot;";
    my $compound = new_math_node();
    $compound->{type} = "block";
    $compound->{value} = [$int, $dots, $int];
    append_math_node($compound);
  }

# ------------------------------------------------------------------------------------------
# h2: Extensible arrows
# ------------------------------------------------------------------------------------------

sub test_extensible_arrow
  {
    my @types =
      (
       ('>>'  ,  '>'   ,  'longrightarrow' , 'subscript'),
       ('>'   ,  '>>'  ,  'longrightarrow' , 'superscript'),
       ('<<'  ,  '<'   ,  'longleftarrow'  , 'subscript'),
       ('<'   ,  '<<'  ,  'longleftarrow'  , 'superscript'),
       ('>>>' ,  ''    ,  'longrightarrow' , ''),
       ('<<<' ,  ''    ,  'longleftarrow'  , '')
      );

    my @special_types =
      (
       ('VV'  ,  'V'   ,  'downarrow'  , 'rightscript'),
       ('V'   ,  'VV'  ,  'downarrow'  , 'leftscript'),
       ('AA'  ,  'A'   ,  'uparrow'    , 'rightscript'),
       ('A'   ,  'AA'  ,  'uparrow'    , 'leftscript'),
       ('AAA' ,  ''    ,  'uparrow'    , ''),
       ('VVV' ,  ''    ,  'downarrow'  , ''),
       ('='   ,  ''    ,  '='          , ''),
      );

    # Have to be enabled, by math_commdiag.mtx.pl for example:
    if ($scan_proc->{extensible_arrow}->{allow_up_down})
      {
	push(@types, @special_types);
      }

    @types = reverse @types;

    while(@types)
      {
	my $start = pop(@types);
	my $end   = pop(@types);
	my $arrow = pop(@types);
	my $type  = pop(@types);

	# Test for arrows without text
	#--------------------------------------------------------------------------
	if ((not $end) && test_regexp('@'.$start))
	  {
	    $scan_proc->{extensible_arrow}->{direction} = $arrow;
	    $scan_proc->{extensible_arrow}->{script} = $type;
	    $scan_proc->{extensible_arrow}->{up_down} = ($start =~ /V|A/);
	    return(TRUE);
	  }

	# Test for arrows with text
	#--------------------------------------------------------------------------
	my $pos = pos(${$scan_proc->{source}});


	# Tester for exp. without {} e.g @>>hello>  or @VVhelloV
	#--------------------------------------------------------------------------
	my $test_shortexp = sub
	                           {
				     my $char = $_[0];
				     if (${$scan_proc->{source}} =~ m/\G([^$char]+)/gmc)
				       {
					 $scan_proc->{last_token} = $1;
					 return TRUE
				       }
				     return FALSE;
				   };

	# Tester for exp. with {}
	#--------------------------------------------------------------------------

	my $ex_char = substr($start,0,1);
	if ($end && test_regexp("@".$start) &&
	                                        ( test_balanced('{','}')          ||
						  &{$test_shortexp}($ex_char)        ))
	  {
	    if ( ${$scan_proc->{source}} !~ m/\G$end/gmc )
	      {
		&{$scan_proc->{error_handler}}("@"."$start not closed correctly ($end) missing");
	      }

	    $scan_proc->{extensible_arrow}->{direction} = $arrow;
	    $scan_proc->{extensible_arrow}->{script} = $type;
	    $scan_proc->{extensible_arrow}->{up_down} = ($start =~ /V|A/);
	    return(TRUE);
	  }
	else
	  {
	   pos(${$scan_proc->{source}}) = $pos;      # To prevent pos change in case of @>> (by test_regexp)
	  }

      }

    return(FALSE);
  }

sub handle_extensible_arrow
  {
    my $direction = $scan_proc->{extensible_arrow}->{direction};
    my $script = $scan_proc->{extensible_arrow}->{script};

    log_message("\nHandle extensible arrow\n");
    log_data(
	     "Direction", $direction,
	     "Script", $script,
	     "Content", $scan_proc->{last_token},
	     "Up_down", ($scan_proc->{extensible_arrow}->{allow_up_down} ? 'Allowed' : 'NOT Allowed'),
	     "Type",($scan_proc->{extensible_arrow}->{up_down} ? 'Up/Down' : 'Left/Right'),
	    );

    # Create the arrow
    #--------------------------------------------------------------
    my $arrow      = new_math_node();
    $arrow->{type} = "operator";
    $arrow->{value} = $cmds_vs_chars{$direction} || $direction;

    # Prepare the node for script mode
    #--------------------------------------------------------------
    my $node       = new_math_node();

    # Set the workaround for to short arrows
    #--------------------------------------------------------------
    $arrow->{minsize}="2em";

    if (not $scan_proc->{extensible_arrow}->{up_down})
      {
	# Add the super or subscript
	#--------------------------------------------------------------
	if ($script)
	  {
	    log_message("\nConverting extensible arrow script (left/right)\n");
	    $node->{type}  = "mover" if ($script eq "superscript");
	    $node->{type}  = "munder" if ($script eq "subscript");
	    my $content_node   = convert_math_as_block(\ ($scan_proc->{last_token}),
						       pos(${$scan_proc->{source}}));
	    $node->{value} = [$arrow, $content_node];
	    log_message("\nResult:\n" . '-' x 30 . "\n");
	    log_math_node($node);
	  }
      }
    else
      {
	# Create a block node to place the right/left script
	#--------------------------------------------------------------
	if ($script)
	  {
	    log_message("\nConverting extensible arrow script (up/down)\n");

	    $sidescript   = convert_math_as_block(\ ($scan_proc->{last_token}), pos(${$scan_proc->{source}}));
	    $sidescript->{maxsize} = "0.6em";

	    $node->{type}  = "block";
	    $node->{value} = [$arrow,  $sidescript] 	if ($script eq "rightscript");
	    $node->{value} = [$sidescript , $arrow] 	if ($script eq "leftscript");

	    log_message("\nResult:\n" . '-' x 30 . "\n");
	    log_math_node($node);
          }
      }

    if ( $script )
      {
	append_math_node($node);
      }
    else
      {
	append_math_node($arrow);
      }
  }

# --------------------------------------------------------------------------------
# h2: \mod, \pmod, \bmod, and \pod commands
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\pmod' command.

sub execute_math_pmod
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_math_pmod (1/2)\n");

    # Create first space node:
    my $space1 = new_math_node();
    $space1->{type} = 'hspace';
    $space1->{value} = $math_mod_space_before;
    $space1->{class} = 'pmod-before';

    # Create 'mod' operator node:
    my $mod = new_math_node();
    $mod->{type} = 'operator';
    $mod->{value} = 'mod';

    # Create second space node:
    my $space2 = new_math_node();
    $space2->{type} = 'hspace';
    $space2->{value} = $math_mod_space_after;
    $space2->{class} = 'pmod-after';

    # Create expression node:
    my $expr = convert_math_as_block(\($man_args->[0]), $pos_man_args->[0]);

    # Create compound node:
    my $compound = new_math_node();
    $compound->{type} = 'block';
    $compound->{left_paren} = '(';
    $compound->{right_paren} = ')';
    $compound->{value} = [$mod, $space2, $expr];

    # Append first space node:
    append_math_node($space1);

    # Append compound node:
    append_math_node($compound);

    log_message("\nexecute_math_pmod (1/2)\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\bmod' command.

sub execute_math_bmod
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_math_bmod (1/2)\n");

    # Create first space node:
    my $space1 = new_math_node();
    $space1->{type} = 'hspace';
    $space1->{value} = $math_mod_space_before_small;
    $space1->{class} = 'bmod-before';

    # Create 'mod' operator node:
    my $mod = new_math_node();
    $mod->{type} = 'operator';
    $mod->{value} = 'mod';

    # Create second space node:
    my $space2 = new_math_node();
    $space2->{type} = 'hspace';
    $space2->{value} = $math_mod_space_after;
    $space2->{class} = 'bmod-after';

    # Append nodes:
    append_math_node($space1);
    append_math_node($mod);
    append_math_node($space2);

    log_message("\nexecute_math_bmod (1/2)\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\mod' command.

sub execute_math_mod
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_math_mod (1/2)\n");

    # Create first space node:
    my $space1 = new_math_node();
    $space1->{type} = 'hspace';
    $space1->{value} = $math_mod_space_before;
    $space1->{class} = 'mod-before';

    # Create 'mod' operator node:
    my $mod = new_math_node();
    $mod->{type} = 'operator';
    $mod->{value} = 'mod';

    # Create second space node:
    my $space2 = new_math_node();
    $space2->{type} = 'hspace';
    $space2->{value} = $math_mod_space_after;
    $space2->{class} = 'mod-after';

    # Append nodes:
    append_math_node($space1);
    append_math_node($mod);
    append_math_node($space2);

    log_message("\nexecute_math_mod (1/2)\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\pod' command.

sub execute_math_pod
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_math_pod (1/2)\n");

    # Create space node:
    my $space = new_math_node();
    $space->{type} = 'hspace';
    $space->{value} = $math_mod_space_before;
    $space->{class} = 'pod';

    # Create expression node:
    my $expr = convert_math_as_block(\($man_args->[0]), $pos_man_args->[0]);
    $expr->{left_paren} = '(';
    $expr->{right_paren} = ')';
    $expr->{class} = 'pod';

    # Append nodes:
    append_math_node($space);
    append_math_node($expr);

    log_message("\nexecute_math_pod (2/2)\n");
  }

# --------------------------------------------------------------------------------
# h2: Numbersets
# --------------------------------------------------------------------------------

sub execute_numberset
  {
    my ($spec, $opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_numberset (1/2)\n");
    log_data('Specifier' , $spec);
    my $node = new_math_node();
    $node->{type} = 'identifier';
    $node->{class} = {'value' => 'number-set', 'stage' => 'VALUE'};
    $node->{value} = $math_numbersets{$spec};
    $node->{superscript} =
      convert_math_as_block(\ ($opt_args->[0]), $pos_opt_args->[0]) if ( $opt_args->[0] );
    append_math_node($node);
    log_message("\nexecute_numberset (2/2)\n");
  }

sub execute_custom_numberset
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_numberset (1/2)\n");
    my $node = new_math_node();
    $node->{type} = 'identifier';
    $node->{class} = 'number-set';
    $node->{value} = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');

    append_math_node($node);
    log_message("\nexecute_numberset (1/2)\n");
  };

# --------------------------------------------------------------------------------
# h2: Marking (\mark and \automark commands)
# --------------------------------------------------------------------------------

sub get_math_mark_class
  {
    my $counter = $_[0];
    if ( $counter !~ m/^[0-9]$/ )
      {
        &{$scan_proc->{error_handler}}
          ("Inavlid marker counter: $counter (expected a number 0 .. 9)");
      }
    return("mark-$counter");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function for the '\mark' command.

sub execute_mark
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_mark (1/2)\n");

    # Get exression to mark:
    my $expr = convert_math_as_block(\($man_args->[0]), $pos_man_args->[0]);

    # Get mark counter:
    my $counter =
      ($opt_args->[0]
       ? get_data_from_arg(0, $opt_args, $pos_opt_args, 'CMD')
       : 0);

    # Set class:
    $expr->{class} = get_math_mark_class($counter);

    # Append expression:
    append_math_node($expr);

    log_message("\nexecute_mark (2/2)\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function for the '\automark' command.

sub execute_automark
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_automark (1/2)\n");

    # Get the target keyword:
    my $target = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    if ( ! grep($_ eq $target,
                'table', 'firstrow', 'lastrow', 'firstcol', 'lastcol', 'diagonal') )
      {
        &{$scan_proc->{error_handler}}("Invalid target: $target");
      }

    # Get mark counter:
    my $counter =
      ($opt_args->[0]
       ? get_data_from_arg(0, $opt_args, $pos_opt_args, 'CMD')
       : 0);

    # Get mark class:
    my $class = get_math_mark_class($counter);

    # Get last math node:
    my $node = get_last_math_node();
    if ( $node->{type} ne 'mtable' )
      {
        &{$scan_proc->{error_handler}}('\automark only allowed after tables or arrays');
      }

    if ( $target eq 'table' )
      {
        append_to_math_node_class($node, $class);
      }
    elsif ( $target eq 'firstrow' )
      {
        append_to_math_node_class($node->{value}->[0], $class);
      }
    elsif ( $target eq 'lastrow' )
      {
        append_to_math_node_class($node->{value}->[$#{$node->{value}}], $class);
      }
    elsif ( $target eq 'firstcol' )
      {
        foreach my $row (@{$node->{value}})
          {
            append_to_math_node_class($row->{value}->[0], $class);
          }
      }
    elsif ( $target eq 'lastcol' )
      {
        foreach my $row (@{$node->{value}})
          {
            append_to_math_node_class($row->{value}->[$#{$row->{value}}], $class);
          }
      }
    elsif ( $target eq 'diagonal' )
      {
        # REMARK: May not work, and cause errors, when \colspan was used
        my $num_rows = scalar(@{$node->{value}});
        my $num_cols = scalar(@{$node->{value}->[0]->{value}});

        if ( $num_rows != $num_cols )
          {
            &{$scan_proc->{error_handler}}
              ('\automark only allowed which square matrices');
          }

        for (my $i = 0; $i < $num_rows; $i++)
          {
            append_to_math_node_class($node->{value}->[$i]->{value}->[$i], $class);
          }
      }

    # Re-append node:
    append_math_node($node);

    log_message("\nexecute_automark (2/2)\n");
}

# --------------------------------------------------------------------------------
# h2: \bra, \ket, and \braket commands
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\bra' command.

sub execute_bra
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_bra (1/2)\n");

    # The '<' symbol:
    my $left = new_math_node();
    $left->{type} = 'operator';
    $left->{value} = '&lt;';

    # The content enclosed by '<' and '|':
    my $content = convert_math_as_block(\($man_args->[0]), $pos_man_args->[0]);

    # The '|' symbol:
    my $right = new_math_node();
    $right->{type} = 'operator';
    $right->{value} = '|';

    # The compound expression:
    my $compound = new_math_node();
    $compound->{type} = 'block';
    $compound->{value} = [$left, $content, $right];

    append_math_node($compound);
    log_message("\nexecute_$mode (2/2)\n");
  };

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\ket' command.

sub execute_ket
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_ket (1/2)\n");

    # The '|' symbol:
    my $left = new_math_node();
    $left->{type} = 'operator';
    $left->{value} = '|';

    # The content enclosed by '|' and '>':
    my $content = convert_math_as_block(\($man_args->[0]), $pos_man_args->[0]);

    # The '>' symbol:
    my $right = new_math_node();
    $right->{type} = 'operator';
    $right->{value} = '&gt;';

    # The compound expression:
    my $compound = new_math_node();
    $compound->{type} = 'block';
    $compound->{value} = [$left, $content, $right];

    append_math_node($compound);
    log_message("\nexecute_$mode (2/2)\n");
  };

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\braket' command.

sub execute_braket
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_bra (1/2)\n");

    # The '<' symbol:
    my $left = new_math_node();
    $left->{type} = 'operator';
    $left->{value} = '&lt;';

    # First entry (content enclosed by '<' and '|'):
    my $entry1 = convert_math_as_block(\($man_args->[0]), $pos_man_args->[0]);

    # The '|' symbol:
    my $middle = new_math_node();
    $middle->{type} = 'operator';
    $middle->{value} = '|';

    # Second entry (content enclosed by '|' and '>'):
    my $entry2 = convert_math_as_block(\($man_args->[0]), $pos_man_args->[0]);

    # The '>' symbol:
    my $right = new_math_node();
    $right->{type} = 'operator';
    $right->{value} = '&gt;';

    # The compound expression:
    my $compound = new_math_node();
    $compound->{type} = 'block';
    $compound->{value} = [$left, $entry1, $middle, $entry2, $right];

    append_math_node($compound);
    log_message("\nexecute_$mode (2/2)\n");
  };

# --------------------------------------------------------------------------------
# h2: Misc. symbols
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\mat' command (symbols denoting matrices).

sub execute_mat
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_mat 1/2\n");

    my $node_list = convert_math_subexp(\($man_args->[0]), $pos_man_args->[0]);

    my $size = scalar(@{$node_list});

    if ( $size == 1 )
      {
	my $mat = $node_list->[0];
	$mat->{class} = 'matrix';
	append_math_node($mat);
      }
    elsif ( $size > 1 )
      {
	my $mat = new_math_node();
	$mat->{type} = 'block';
	$mat->{class} = 'matrix';
	$mat->{value} = $node_list;
	append_math_node($mat);
      }

    log_message("\nexecute_mat 2/2\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\linmat' command (symbols denoting linear spaces of matrices).

sub execute_linmat
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_linmat 1/2\n");

    my $node_list = convert_math_subexp(\($man_args->[0]), $pos_man_args->[0]);

    my $size = scalar(@{$node_list});

    if ( $size == 1 )
      {
	my $linmat = $node_list->[0];
	$linmat->{class} = 'lin-space-matrix';
	append_math_node($linmat);
      }
    elsif ( $size > 1 )
      {
	my $linmat = new_math_node();
	$linmat->{type} = 'block';
	$linmat->{class} = 'lin-space-matrix';
	$linmat->{value} = $node_list;
	append_math_node($linmat);
      }

    log_message("\nexecute_linmat 2/2\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\vectorspace' command.

sub execute_vectorspace
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_vectorspace 1/2\n");

    my $block = new_math_node();
    $block->{type} = 'block';
    $block->{class} = 'vector-space';

    my $symbol = convert_math_as_block(\ ($man_args->[0]), $pos_man_args);

    if ($opt_args->[0])
      {
        my $field = convert_math_as_block(\ ($opt_args->[0]), $pos_opt_args);
	$block->{value} = [$field, $symbol];
      }
    else
      {
	$block->{value} = [$symbol];
      }

    append_math_node($block);

    log_message("\nexecute_vectorspace 2/2\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\affinespace' command.

sub execute_affinespace
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_affinespace 1/2\n");

    my $block = new_math_node();
    $block->{type} = 'block';
    $block->{class} = 'affine-space';

    my $symbol = convert_math_as_block(\ ($man_args->[0]), $pos_man_args);

    if ($opt_args->[0])
      {
        my $field = convert_math_as_block(\ ($opt_args->[0]), $pos_opt_args);
	$block->{value} = [$field, $symbol];
      }
    else
      {
	$block->{value} = [$symbol];
      }

    append_math_node($block);

    log_message("\nexecute_affinespace 2/2\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\fourier' command.

sub execute_fourier
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_fourier (1/2)\n");

    my $result =  new_math_node();
    $result->{type} = "identifier";
    $result->{value} = "F";
    $result->{class} = "fourier";
    append_math_node($result);

    log_message("\nexecute_fourier (2/2)\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\decimal' command. Currently, this command does not
# do anything but printing out its argument.

sub execute_decimal
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_decimal 1/2\n");
    my $dec = convert_math_as_block(\ ($man_args->[0]), $pos_man_args->[0]);
    append_math_node($dec);
    log_message("\nexecute_decimal 2/2\n");
  }

# --------------------------------------------------------------------------------
# h2: Command- and environment tables for export
# --------------------------------------------------------------------------------

sub create_math_env_table
  {
    my $math_env_table =
      {
       "displaymath" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "begin_function" => \&begin_displaymath,
	  "end_function" =>  \&end_displaymath,
	 },
       "equation" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "begin_function" => \&begin_equation,
	  "end_function" => \&end_equation,
	 },
       "equation*" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "begin_function" => \&begin_equation_star,
	  "end_function" => \&end_equation_star,
	 },
      };
    return $math_env_table;
  }

sub create_math_cmd_table
  {
    my $math_cmd_table =
      {
       "id" =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 0,
          "execute_function" => \&execute_math_id,
          "doc" =>
            {
	     "opt_args" =>[["set", "Domain"]],
	     "description" => "Identical map. %[0] is its domain.",
	    }
         },

       "mbox" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => \&execute_math_normal_text,
	  "doc" =>
	    {
	     "description" => 'Text in math mode',
	     "man_args" => [["text", "text"]],
	    }
	 },
       "text" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => \&execute_math_normal_text,
	  "doc" =>
	    {
	     "description" => 'Text in math mode',
	     "man_args" => [["text", "text"]],
	    }
	 },
       "[" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 0,
          "execute_function" => sub {Mumie::MmTeX::Parser::process_env_begin("displaymath");},
          "doc" =>
            {
	     "description" => 'Same as \begin{displaymath}'
	    }
         },
       "]" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 0,
          "execute_function" => sub {Mumie::MmTeX::Parser::process_env_end("displaymath");},
          "doc" =>
	    {
	     "description" => 'Same as \end{displaymath}'
	    }
         },
       "vector" =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 1,
          "execute_function" => \&execute_vector,
          "doc" =>
            {
	     "man_args" => [["content","Content of the vector"]],
	     "opt_args" => [["style","Style, 'r' for row, 'c' for col (default)"]],
	     "description" => "Math vector, 'cells' divided by \\\\.",
	    }
         },
       "frac" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 2,
          "execute_function" => sub {execute_frac('center',@_)},
          "doc" =>
            {
	     "man_args" => [["num", "Numerator"], ["den", "Denominator"]],
	     "description" => "Math fraction",
	    }
         },
       "cfrac" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 2,
          "execute_function" => sub {execute_frac('center',@_)},
          "doc" =>
            {
	     "man_args" => [["num", "Numerator"], ["den", "Denominator"]],
	     "description" => "Math fraction, the same as 'frac'",
	     "see" => {"cmds" => ["frac"]},
	    }
         },
       "lcfrac" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 2,
          "execute_function" => sub {execute_frac('left',@_)},
          "doc" =>
            {
	     "man_args" => [["num", "Numerator"], ["den", "Denominator"]],
	     "description" => "Math fraction, numerator left align",
	     "see" => {"cmds" => ["frac"]},
	    }
         },
       "rcfrac" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 2,
          "execute_function" => sub {execute_frac('right',@_)},
          "doc" =>
	    {
	     "man_args" => [["num", "Numerator"], ["den", "Denominator"]],
	     "description" => "Math fraction, numerator rigth align",
	     "see" => {"cmds" => ["frac"]},
	    }
         },
       "sqrt" =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 1,
          "execute_function" => \&execute_sqrt,
          "doc" =>
            {
	     "opt_args" => [["deg", "Degree"]],
	     "man_args" => [["rad", "Radicant"]],
	     "description" => "Math root",
	    }
         },
       "norm" =>
         {
	  "num_opt_args" => 1,
	  "num_man_args" => 1,
	  "execute_function" => \&execute_norm,
	  "doc" =>
    	    {
	     "opt_args" => [["special style","may be used for custom style"]],
	     "man_args" => [["content", "content"]],
	     "description" => "Norm of a vector",
	     "see" => {"cmds" =>["vecstyle"]},
	    }
	 },
       'norm*' =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => \&execute_norm_star,
	  "doc" =>
    	    {
	     "man_args" => [["content", "content"]],
	     "description" => "Norm of a vector, style will be R. Short for norm[R]",
	    }
	 },

       "vec" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => \&execute_vec,
	  "doc" =>
    	    {
	     "man_args" => [["content", "content"]],
	     "description" => "Vector, style may be set via vectstyle command",
	     "see" => {"cmds" =>["vecstyle"]},
	    }
	 },
       "mat" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => \&execute_mat,
	  "doc" =>
    	    {
	     "man_args" => [["content", "content"]],
	     "description" => "Matrix, will be marked via class attr.",
	    }
	 },
       "linmat" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => \&execute_linmat,
	  "doc" =>
    	    {
	     "man_args" => [["content", "content"]],
	     "description" => "Linear matrix, will be marked via class attribute",
	     "see" => {"cmds" =>["vecstyle"]},
	    }
	 },

       "limits" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => \&execute_limits,
	  "doc" =>
    	    {
	     "description" => "Turns sub(super)script to limits",
	     "see" => {"cmds" => ["nolimits"]},
	    }
	 },
       "nolimits" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => \&execute_nolimits,
	  "doc" =>
    	    {
	     "description" => "Turns limits to sub(super)script",
	     "see" => {"cmds" => ["limits"]},
	    }
	 },
       "quad" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => sub {  execute_math_space_cmd(@_, '1em') },
	  "doc" =>
    	    {
	     "description" => "Space (1em)",
	     "see" => {"cmds" => ["qquad", ",", ";", ":", "!"]},
	    }
	 },
       "qquad" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => sub {  execute_math_space_cmd(@_, '2em') },
	  "doc" =>
    	    {
	     "description" => "Space (2em)",
	     "see" => {"cmds" => ["qquad", ",", ";", ":", "!"]},
	    }
	 },
       "," =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => sub {  execute_math_space_cmd(@_, '0.167em') },
	  "doc" =>
    	    {
	     "description" => "Small space (3/18em)",
	     "see" => {"cmds" => ["quad", "qquad", ";", ":", "!"]},
	    }
	 },
       ":" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => sub {  execute_math_space_cmd(@_, '0.222em') },
	  "doc" =>
    	    {
	     "description" => "Medium space (4/18em)",
	     "see" => {"cmds" => ["quad", "qquad", ",", ";", "!"]},
	    }
	 },
       ";" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => sub {  execute_math_space_cmd(@_, '0.278em') },
	  "doc" =>
    	    {
	     "description" => "Large space (5/18em)",
	     "see" => {"cmds" => ["quad", "qquad", ",", ":", "!"]},
	    }
	 },
       "!" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => sub {  execute_math_space_cmd(@_, '-0.167em') },
	  "doc" =>
    	    {
	     "description" => "Negative space (-3/18em)",
	     "see" => {"cmds" => ["quad", "qquad", ",", ":", ";"]},
	    }
	 },
       "hspace" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => \&execute_math_hspace,
	  "doc" =>
    	    {
	     "description" => "User defined horizontal space",
	     "man_args" => [["space","space value"]],
	     "see" => {"cmds" => ["quad", "qquad", ";", ":", "!", "vspace"]},
	    }
	 },
       "{" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => \&execute_curly_paren_left,
	  "doc" =>
    	    {
	     "description" => "Start a region in {} parenthesis",
	    }
	 },
       "}" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => \&execute_curly_paren_right,
	  "doc" =>
    	    {
	     "description" => "Closes a region in {} parenthesis",
	    }
	 },
       "left" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => \&execute_left,
	  "doc" =>
    	    {
	     "description" => "Left delimiter",
	    }
	 },
       "right" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => \&execute_right,
	  "doc" =>
    	    {
	     "description" => "Right delimiter",
	    }
	 },
       "hat" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_accent_cmd(@_, '&Hat;') },
	  "doc" =>
    	    {
	     "description" => "Hat above (wide if argument char count > 1)",
	     "man_args" =>  [["content","content"]],
	    }
         },
       "widehat" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_accent_cmd(@_, '&Hat;') },
	  "doc" =>
    	    {
	     "description" => "Hat above (wide if argument char count > 1)",
	     "man_args" => [["content","content"]],
	    }
         },
       "acute" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_accent_cmd(@_, '&acute;') },
	  "doc" =>
    	    {
	     "description" => "Up-accent above",
	     "man_args" => [["content","content"]],
	    }
         },
       "bar" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_accent_cmd(@_, '&OverBar;') },
	  "doc" =>
    	    {
	     "description" => "Bar above",
	     "man_args" => [["content","content"]],
	    }
         },
       "dot" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_accent_cmd(@_, '&dot;') },
	  "doc" =>
    	    {
	     "description" => "Dot above",
	     "man_args" => [["content","content"]],
	    }
         },
       "breve" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_accent_cmd(@_, '&breve;') },
	  "doc" =>
    	    {
	     "description" => "Breve (Smile-accent) above",
	     "man_args" => [["content","content"]],
	    }
         },
       "check" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_accent_cmd(@_, '&Hacek;') },
	  "doc" =>
    	    {
	     "description" => "Inverted hat above",
	     "man_args" => [["content","content"]],
	    }
         },
       "grave" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_accent_cmd(@_, '&grave;') },
	  "doc" =>
    	    {
	     "description" => "Down accent above",
	     "man_args" => [["content","content"]],
	    }
         },
       "ddot" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_accent_cmd(@_, '&die;') },
	  "doc" =>
    	    {
	     "description" => "Two dots above",
	     "man_args" => [["content","content"]],
	    }
         },
       "dddot" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_accent_cmd(@_, '&tdot;') },
	  "doc" =>
    	    {
	     "description" => "Three dots above",
	     "man_args" => [["content","content"]],
	    }
         },
       "ddddot" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,    # Mozilla Problem (with &#x20DC; too)
	  "execute_function" => sub { execute_math_accent_cmd(@_, '&DotDot;') },
	  "doc" =>
    	    {
	     "description" => "Four dots above",
	     "man_args" => [["content","content"]],
	    }
         },

       "fourier" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 0,
	  "execute_function" => \&execute_fourier,
	  "doc" =>
    	    {
	     "description" => "Double struck F for fourier transformation",
	    }
         },

       "tilde" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_accent_cmd(@_, '&tilde;') },
	  "doc" =>
    	    {
	     "description" => "Tilde above (wide if argument char count > 1)",
	     "man_args" => [["content","content"]],
	    }
         },
       "widetilde" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_accent_cmd(@_, '&tilde;') },
	  "doc" =>
    	    {
	     "description" => "Tilde above (wide if argument char count > 1)",
	     "man_args" => [["content","content"]],
	    }
         },
       "overleftarrow" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_accent_cmd(@_, '&leftarrow;') },
	  "doc" =>
    	    {
	     "description" => "Right-to-Left arrow above",
	     "man_args" => [["content","content"]],
	    }
         },
       "overrightarrow" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_accent_cmd(@_, '&rightarrow;') },
	  "doc" =>
    	    {
	     "description" => "Left-to-Right arrow above",
	     "man_args" => [["content","content"]],
	    }
         },
       "overline" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_overset_cmd(@_, '&OverBar;') },
	  "doc" =>
    	    {
	     "description" => "Line over an expression",
	     "man_args" => [["expression","expression"]],
	    }
         },
       "overbrace" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_overset_cmd(@_, '&OverBrace;', undef(), TRUE) },
	  "doc" =>
    	    {
	     "description" => "Brace over an expression",
	     "man_args" => [["expression","expression"]],
	    }
         },
       "cnum" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 2,
	  "execute_function" => \&execute_cnum,
	  "doc" =>
    	    {
	     "man_args" => [["re_part","real part of complex number"], ["cm_part", "complex part"]],
	     "description" => "Complex numbers",
	    }
         },
       "shallbe" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => sub {execute_math_overset_cmd(@_, '!', "=")},
	  "doc" =>
    	    {
	     "description" => "Shallbe sign (! over =)",
	    }
         },
       "underline" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_underset_cmd(@_, '&OverBar;') },
	  "doc" =>
    	    {
	     "man_args" => [["expression","expression"]],
	     "description" => "Line under an expression",
	    }
         },
       "underbrace" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_underset_cmd(@_, '&UnderBrace;', undef(), TRUE) },
	  "doc" =>
    	    {
	     "man_args" => [["expression","expression"]],
	     "description" => "Brace under an expression",
	    }
         },
       "implies" =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 0,
          "execute_function" => sub { execute_implies(@_) },
          "doc" =>
            {
	     "opt_args" => [["direction",
			     "'r' [default]: =>; 'l': <=; 't|u': arrow up; 'b|d': arrow down."]],
	     "description" => "Shortcut of 'Rightarrow', 'Leftarrow' ..",
	    }
         },
       "longimplies" =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 0,
          "execute_function" => \&execute_longimplies,
          "doc" =>
            {
	     "opt_args" => [["direction",
			     "'r' [default]: =>; 'l': <=; 't|u': arrow up; 'b|d': arrow down."]],
	     "description" => "Shortcut of 'Longrightarrow', 'Longleftarrow' ..",
	    }
         },
       "iff" =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 0,
          "execute_function" => \&execute_iff,
          "doc" =>
            {
	     "opt_args" => [["direction",
			     "'r|l|h' [default]: <=>; 'u|d|v': arrow updown."]],
	     "description" => "Shortcut of 'Leftrightarrow' and  'Updownarrow'",
	    }
         },
       "longiff" =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 0,
          "execute_function" => \&execute_longiff,
          "doc" =>
            {
	     "opt_args" => [["direction",
			     "'r|l|h' [default]: long <=>; 'u|d|v': (short) arrow updown."]],
	     "description" => "Shortcut of 'Longleftrightarrow' and  'Updownarrow'",
	    }
         },
       "arrowvert" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 0,
          "execute_function" => \&execute_arrowvert,
          "doc" =>
            {
	     "description" => "The same like | but a little bigger",
	    }
         },
       "Arrowvert" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 0,
          "execute_function" => \&execute_Arrowvert,
          "doc" =>
            {
	     "description" => "The same like \| but a little bigger",
	    }
         },
       "binom" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 2,
          "execute_function" => \&execute_binom,
          "doc" =>
            {
	     "description" => "For binomial expressions, with normal brackets",
	     "man_args" => [["upper","upper expression"],["lower","lower expression"]],
	    }
         },
       "inner" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 2,
          "execute_function" => \&execute_inner,
          "doc" =>
            {
	     "man_args" =>[["arg1", "arg1"], ["arg2", "arg2"]],
	     "description" => "Scalar product",
	    }
         },
       "numberset" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "execute_function" => \&execute_custom_numberset,
          "doc" =>
            {
	     "man_args" => [["char", "char"]],
	     "description" => "Custom numberset",
	    }
         },
       "overset" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 2,
          "execute_function" => \&execute_math_overset_cmd,
          "doc" =>
            {
	     "man_args" => [["argument", "Expression to be placed over the base"],
			    ["base", "base expression"]],
	     "description" => "Places the argument over the base.",
	    }
         },
       "underset" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 2,
	  "execute_function" => \&execute_math_underset_cmd,
          "doc" =>
            {
	     "man_args" => [["argument", "Expression to be placed under the base"],
			    ["base", "base expression"]],
	     "description" => "Places the argument under the base.",
	    }
         },
       "pmod" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "execute_function" => \&execute_math_pmod,
          "doc" =>
            {
	     "man_args" => [["argument", "argument"]],
	     "description" => "Places 'mod' in front of the argument and".
	     " brackets around the whole expression and adjust spacing",
	    }
         },
       "mod" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 0,
          "execute_function" => \&execute_math_mod,
          "doc" =>
            {
	     "man_args" => [["argument", "argument"]],
	     "description" => "Places 'mod' in front of the argument and adjust spacing",
	    }
         },
       "bmod" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 0,
          "execute_function" => \&execute_math_bmod,
          "doc" =>
            {
	     "description" => "Places 'mod' operator",
	    }
         },
       "pod" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "execute_function" => \&execute_math_pod,
          "doc" =>
            {
	     "man_args" => [["argument", "argument"]],
	     "description" => "Handle the special spacing of the mod operator, adds brackets",
	    }
         },
       "varlimsup" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 0,
          "execute_function" => sub { execute_math_overset_cmd(@_, '&OverBar;', 'lim') },
          "doc" =>
            {
	     "description" => "Short way to add a bar over the limes"
	    }
         },
       "varliminf" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 0,
          "execute_function" => sub { execute_math_underset_cmd(@_, '&UnderBar;', 'lim') },
          "doc" =>
            {
	     "description" => "Short way to add a bar under the limes"
	    }
         },
       "vectorspace" =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 1,
          "execute_function" => \&execute_vectorspace,
          "doc" =>
            {
	     "man_args" => [["char", "char as vectorspace"]],
	     "opt_args" => [["???", "???"]],
	     "description" => "A math identifier as vectorspace",
	    }
         },
       "affinespace" =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 1,
          "execute_function" => \&execute_affinespace,
          "doc" =>
            {
	     "man_args" => [["char", "char as affinespace"]],
	     "opt_args" => [["???", "???"]],
	     "description" => "A math identifier as affinespace",
	    }
         },

       "vecstyle" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "execute_function" => \&execute_vecstyle,
          "doc" =>
            {
	     "man_args" => [["style", "Style"]],
	     "description" =>
	       'Sets the style for the next \\vec command. Should be one ' .
	       'of the following: "arrow" "plain" "frakt" "underline" "bold" ' .
	       '"bold-frakt" "bold-italic" "script" "italic" "double-struck" ' .
	       '"sans-serif" "monospace".',
	    }
         },
       "decimal" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "execute_function" => \&execute_decimal,
          "doc" =>
            {
	     "description" => 'Custom international decimal rendering (IN WORK, no effect yet).',
	     "man_args" => [["argument", "argument"]],
	    }
         },
       "idotsint" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 0,
          "execute_function" => \&execute_idotsint,
          "doc" =>
            {
	     "description" => 'Integral sign, followed by dots, followed by another int sign'
	    }
         },
       "mathring" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "execute_function" => sub { execute_math_accent_cmd(@_, '&SmallCircle;') },
          "doc" =>
            {
	     "man_args" => [["argument", "Ring will be placed above this sign"]],
	     "description" => 'A letter with a ring above it',
	    }
         },
       "conj" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => sub { execute_math_accent_cmd(@_, '&OverBar;') },
	  "doc" =>
    	    {
	     "description" => "Conjugate, temporary workaround mapping command to 'bar above'",
	     "man_args" => [["argument", "Bar will be placed over the argument"]],
	    }
         },
       "xleftarrow" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => sub
	                         {
				   $scan_proc->{extensible_arrow}->{direction}= 'longleftarrow';
				   $scan_proc->{extensible_arrow}->{script} = '';
				   handle_extensible_arrow();
				 },
	  "doc" =>
    	    {
	     "description" => "Long double arrow from right to left, like @<<<",
	    }
	 },
       "xrightarrow" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => sub
	                         {
				   $scan_proc->{extensible_arrow}->{direction}= 'longrightarrow';
				   $scan_proc->{extensible_arrow}->{script} = '';
				   handle_extensible_arrow();
				 },
	  "doc" =>
    	    {
	     "description" => "Long double arrow from left to right, like @>>>",
	    }
	 },
       "span" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => sub
	                         {
				   execute_math_function_name_cmd(@_, 'Lin', 'span');
				 },
	  "doc" =>
    	    {
	     "description" => "Math function name, will output Lin",
	    }
	 },
       "lin" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => sub
	                         {
				   execute_math_function_name_cmd(@_, 'Lin', 'lin');
				 },
	  "doc" =>
    	    {
	     "description" => "Math function name, will output Lin",
	    },
	 },
       "cal" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => \&execute_cal,
	  "doc" =>
    	    {
	     "description" => "Script (or calligraphic) symbols.",
	     "man_args"    => [["text","only A-Z a-z allowed"]],
	    },
	 },
       "ket" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => \&execute_ket,
	  "doc" =>
    	    {
	     "description" => "Result will be   | arg >",
	     "man_args"    => [["arg","argument"]],
	    },
	 },
       "bra" =>
         {
	  "num_opt_args" => 0,
	  "num_man_args" => 1,
	  "execute_function" => \&execute_bra,
	  "doc" =>
    	    {
	     "description" => "Result will be   | arg >",
	     "man_args"    => [["arg","argument"]],
	    },
	 },
       "braket" =>
       {
	  "num_opt_args" => 0,
	  "num_man_args" => 2,
	  "execute_function" => \&execute_braket,
	  "doc" =>
    	    {
	     "description" => "Result will be < 1.arg  | 2.arg >",
	     "man_args"    => [["arg","first argument"], ["arg","second argument"]],
	    },
	 },
       "iint" =>
        {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => sub { execute_int(@_, 2) },
	  "doc" =>
    	    {
	     "description" => "Multiple integral signs",
	    },
        },
       "iiint" =>
        {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => sub { execute_int(@_, 3) },
	  "doc" =>
    	    {
	     "description" => "Multiple integral signs",
	    },
       },
       "iiiint" =>
        {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => sub { execute_int(@_, 4) },
	  "doc" =>
    	    {
	     "description" => "Multiple integral signs",
	    },
       },
       "iiiiint" =>
        {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => sub { execute_int(@_, 5) },
	  "doc" =>
    	    {
	     "description" => "Multiple integral signs",
	    },
        },
       "iiiiiint" =>
        {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "execute_function" => sub { execute_int(@_, 6) },
	  "doc" =>
    	    {
	     "description" => "Multiple integral signs",
	    },
        },
       "abs" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "execute_function" => \&execute_abs,
          "doc" =>
            {
	     "man_args" => [["argument", "argument"]],
	     "description" => "Addes |a| to a (for absolute value)",
	    }
         },
       "mark" =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 1,
          "execute_function" => \&execute_mark,
          "doc" =>
            {
	     "man_args" => [["Expression", "Expression to highlight"]],
	     "opt_args" => [["Number", "Argument 1-9 (0 if omitted) to define style of highlighting"]],
	     "description" => "Marks an expression with a predefined style (0-9) to create logic cohesions.".
	                      "See automark for an easy way to highlight a row or a matrix diagonal",
	    }
         },
       "automark" =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 1,
          "execute_function" => \&execute_automark,
          "doc" =>
            {
	     "man_args" => [["Mode", "Automark mode"]],
	     "opt_args" => [["Number", "Argument 1-9 (0 if omitted) to define style of highlighting"]],
	     "description" => "Marks a table row or col, should accour directly after a mtable, mandatory arguments are:".
	                      "first_row, last_row, first_col, last_col, diagonal, table",
	    }
         },

      };

    foreach my $cmd (keys(%cmds_vs_chars))
      {
        my $char = $cmds_vs_chars{$cmd};
        $math_cmd_table->{$cmd} =
          {
           "num_opt_args" => 0,
           "num_man_args" => 0,
           "execute_function" => sub { execute_math_special_char_cmd($cmd, @_) },
           "category" => "math_special_char",
           "node_type" => "operator",
           "char" => $char,
           "can_have_limits" => FALSE,
           "doc" =>
    	     {
              "description" => "Math special character",
             }
          };
      }

    foreach my $function_name (@function_names)
      {
        my $cmd_name = $function_name;
        $math_cmd_table->{$function_name} =
          {
           "num_opt_args" => 0,
           "num_man_args" => 0,
           "execute_function" =>
             sub { execute_math_function_name_cmd(@_, $function_name, $cmd_name) },
           "category" => "math_function_name",
           "doc" =>
             {
    	  "description" => "Math function name",
    	 }
          }
      }

    foreach my $cmd (@cmds_that_can_have_limits)
      {
        $math_cmd_table->{$cmd}->{can_have_limits} = TRUE;
        $math_cmd_table->{$cmd}->{limit_mode}
          =
    	{
    	 "INLINE" => "BEHIND",
    	 "BLOCK" => "UNDER_OVER",
    	};
      }

    foreach my $cmd (@cmds_that_are_identifiers)
      {
        $math_cmd_table->{$cmd}->{node_type} = "identifier";
      }

    foreach my $cmd (@greek_chars)
      {
        my $char = lc($cmd);
        my $description =
          ( $cmd =~ m/^[A-Z]/ ? "Capital" : "Lower case" ) . " greek $char";
        $math_cmd_table->{$cmd}->{doc}->{description} = $description;
      }

    foreach my $ns (keys %math_numbersets)
      {
	$math_cmd_table->{"$ns"} =
	  {
	   "num_opt_args" => 1,
	   "num_man_args" => 0,
	   "execute_function" => sub {execute_numberset($ns, @_)},
	   "doc" =>
	     {
	      "opt_args" => [["dimension", "Dimension of the numberset"]],
	      "description" => "Numberset ($ns)",
	     }
	  };
      }

    return $math_cmd_table;
  };

# --------------------------------------------------------------------------------
# h2: Tables (should be moved to math_table)
# --------------------------------------------------------------------------------

sub handle_math_table_value
  {
    my $node = $_[0];
    my $value = $node->{value};
    my $left_paren = ( exists($node->{left_paren}) ? $node->{left_paren} : "" );
    my $right_paren = ( exists($node->{right_paren}) ? $node->{right_paren} : "" );

    my $attribs = math_default_attribs($node);

    log_message("\nhandle_math_table_value\n");
    log_data("Left paren", $left_paren,
	     "Right paren", $right_paren,
	     "Paren mode", $mathml_paren_mode) ;


    if ( $mathml_paren_mode eq 'PLAIN' )
      {
	if ( ( $left_paren ) || ( $right_paren ) )
	  {
            # Left parenthesis:
	    start_mathml_element('mrow', $attribs, 'DISPLAY');
	    start_mathml_element('mo', {}, 'SEMI_DISPLAY');
	    xml_pcdata($left_paren) if ( $left_paren );
	    close_mathml_element('mo', 'INLINE');

            # The table itself:
	    start_mathml_element('mtable', $attribs, 'DISPLAY');
	    handle_math_node_list($value);
	    close_mathml_element('mtable', 'DISPLAY');

            # Right parenthesis:
	    start_mathml_element("mo", {}, "SEMI_DISPLAY");
	    xml_pcdata($right_paren) if ( $right_paren );
	    close_mathml_element("mo", "INLINE");
	    close_mathml_element('mrow', 'DISPLAY');

	  }
	else
	  {
	    start_mathml_element('mtable', $attribs, 'DISPLAY');
	    handle_math_node_list($value);
	    close_mathml_element('mtable', 'DISPLAY');
	  }
      }
    elsif ( $mathml_paren_mode eq 'MFENCED' )
      {
        # Start enclosing "mfenced":
	$attribs->{open} = $left_paren;
	$attribs->{close} = $right_paren;
	start_mathml_element('mfenced', $attribs, 'DISPLAY');

        # The table itself:
	start_mathml_element('mtable', $attribs, 'DISPLAY');
	handle_math_node_list($value);
	close_mathml_element('mtable', 'DISPLAY');

        # Clode enclosing "mfenced":
	close_mathml_element('mfenced', 'DISPLAY');
      }
  };

sub handle_math_table_row_value
  {
    my $node = $_[0];
    my $value = $node->{value};
    log_message("\nhandle_math_table_row_value\n");
    start_mathml_element('mtr', math_default_attribs($node), 'DISPLAY');
    handle_math_node_list($value);
    close_mathml_element('mtr', 'DISPLAY');
  };

sub handle_math_table_cell_value
  {
    my $node = $_[0];
    my $value = $node->{value};
    my $attribs = math_default_attribs($node);

    $attribs ||= {};

    log_message("\nhandle_math_table_cell_value\n");
    start_mathml_element('mtd', $attribs, 'DISPLAY');

    if ($#{$value} > 0)
      {
	start_mathml_element("mrow", {}, "DISPLAY");
	handle_math_node_list($value);
	close_mathml_element("mrow");
      }
    else
      {
	handle_math_node_list($value);
      }

    close_mathml_element('mtd', 'DISPLAY');
  };

# ==========================================================================================
# h1: Initializing the library
# ==========================================================================================

# --------------------------------------------------------------------------------
# h2: Math tokens
# --------------------------------------------------------------------------------

#Ds
#a ()
# Sets the global variables $math_token_table and @math_token_types.

sub init_math_tokens
  {
    log_message("\ninit_math_tokens\n");
    $math_token_table = $default_token_table;

    my $_inline_math_boundry = "(?<!\\\\)\\\$";
    my $_math_ign_whitesp = "\\s+";
    my $_math_identifier = "[a-zA-Z]";
    my $_math_number = "[0-9]+(?:\\.[0-9]+)?";
    my $_math_operator = "[-+*/!=.,;:|']";
    my $_math_special_operator = "[><]";
    my $_math_sign = "[+-]";

    $math_token_table->{inline_math_boundry} =
      {
	 "tester" => sub { test_regexp("$_inline_math_boundry") },
	 "handler" => \&handle_inline_math_boundry,
	 "is_par_starter" => TRUE,
	};

    $math_token_table->{math_number} =
      {
       "tester" => sub
         {
	   if ( ! @{$scan_proc->{math_node_list}} )
	     {
	       test_regexp("$_math_sign?$_math_number");
	     }
	   else
	     {
	       test_regexp("$_math_number");
	     }
	 },
       "handler" => sub
	 {
	   my $node = new_math_node();
	   $node->{type} = "number";
	   $node->{value} = $scan_proc->{last_token};
	   append_math_node($node);
	 },
	};

    $math_token_table->{math_identifier} =
      {
       "tester" => sub { test_regexp("$_math_identifier") },
       "handler" => sub
         {
	   my $node = new_math_node();
	   $node->{type} = "identifier";
	   $node->{value} = $scan_proc->{last_token};
	   append_math_node($node);
	 },
      };

    $math_token_table->{math_operator} =
      {
       "tester" => sub { test_regexp("$_math_operator") },
       "handler" => sub
	 {
	   my $node = new_math_node();
	   $node->{type} = "operator";
	   $node->{value} = $scan_proc->{last_token};
	   append_math_node($node)
	 },
      };

    $math_token_table->{math_special_operator} =
      {
       "tester" => sub { test_regexp("$_math_special_operator") },
       "handler" => sub
	 {
	   my $special_operators = { '<' => "&lt;" ,
				     '>' => "&gt;" ,};

	   if (not $special_operators->{$scan_proc->{last_token}})
	     {
	       &{$scan_proc->{error_handler}}("Unknown math_special_operator "
					      . $scan_proc->{last_token});
	     }
	   my $node = new_math_node();
	   $node->{type} = "operator";
	   $node->{value} = $special_operators->{$scan_proc->{last_token}};
	   append_math_node($node)
	 },
      };

    $math_token_table->{math_subscr} =
      {
       "tester" => sub { test_regexp("_") },
       "handler" => sub
	 {
	   if ( $scan_proc->{math_mode} eq "BASE" )
	     {
	       $scan_proc->{math_mode} = "SUBSCRIPT";
	     }
	   else
	     {
	       &{$scan_proc->{error_handler}}("No base for subscript.");
	     }
	 },
      };

    $math_token_table->{math_superscr} =
      {
       "tester" => sub { test_regexp("\\^") },
       "handler" => sub
	 {
	   if ( $scan_proc->{math_mode} eq "BASE" )
	     {
	       $scan_proc->{math_mode} = "SUPERSCRIPT";
	     }
	   else
	     {
	       &{$scan_proc->{error_handler}}("No base for superscript.");
	     }
	 },
      };

    $math_token_table->{math_block_start} =
      {
       "tester" => sub { test_regexp("(?<!\\\\){") },
       "handler" => sub { start_math_block("_math block") }
      };

    $math_token_table->{math_block_end} =
      {
       "tester" => sub { test_regexp("(?<!\\\\)}") },
       "handler" => sub { close_math_block("_math block") }
      };

    $math_token_table->{math_left_paren} =
      {
       "tester" => sub { test_regexp("[(\\[]") }, # (?:\\(|\\{|\\[)
       "handler" => sub
	 {
	   if ( $scan_proc->{math_paren_mode} eq 'BLOCK' )
	     {
	       my $left_paren = $scan_proc->{last_token};
	       my $right_paren = $other_paren{$left_paren};
	       my $pseudo_env_name
		 = "_math " . $left_paren . $right_paren . " parenthesis";
	       start_math_block($pseudo_env_name);
	     }
	   elsif ( $scan_proc->{math_paren_mode} eq 'SINGLE' )
	     {
	       my $node = new_math_node();
	       $node->{type} = "operator";
	       $node->{value} = $scan_proc->{last_token};
	       append_math_node($node)
	     }
	 }
      };

    $math_token_table->{math_right_paren} =
      {
       "tester" => sub { test_regexp("[)\\]]") }, # (?:\\)|\\}|\\])
       "handler" => sub
	 {
	   if ( $scan_proc->{math_paren_mode} eq 'BLOCK' )
	     {
	       my $right_paren = $scan_proc->{last_token};
	       my $left_paren = $other_paren{$right_paren};
	       my $pseudo_env_name
		 = "_math " . $left_paren . $right_paren . " parenthesis";
	       close_math_block($pseudo_env_name, $left_paren, $right_paren);
	     }
	   elsif ( $scan_proc->{math_paren_mode} eq 'SINGLE' )
	     {
	       my $node = new_math_node();
	       $node->{type} = "operator";
	       $node->{value} = $scan_proc->{last_token};
	       append_math_node($node)
	     }
	 }
      };

    $math_token_table->{math_ign_whitesp} =
      {
       "tester" => sub { test_regexp("$_math_ign_whitesp") },
       "handler" => sub {},
      };

    $math_token_table->{math_tbl_sep} =
      {
       "tester" => sub { test_regexp("&") },
       "handler" => sub
         {
	   &{$scan_proc->{error_handler}}
	     ('Math table cell separator not allowed here.');
	 },
      };

    $math_token_table->{math_extensible_arrow} =
      {
       "tester" =>  \&test_extensible_arrow,
       "handler" => \&handle_extensible_arrow,
      };

    @math_token_types =
      (
       "math_extensible_arrow",
       "cmd",
       "one_char_cmd",
       "comment",
       "inline_math_boundry",
       "math_ign_whitesp",
       "math_identifier",
       "math_number",
       "math_operator",
       "math_special_operator",
       "math_subscr",
       "math_superscr",
       "math_block_start",
       "math_block_end",
       "math_left_paren",
       "math_right_paren",
      );
  }

# --------------------------------------------------------------------------------
# h2: Default math node table
# --------------------------------------------------------------------------------

sub init_default_math_nodes
  {
    log_message("\ninit_default_math_nodes\n");
    $default_math_nodes =
      {
       'identifier' =>
         {
	  "value_type" => "TEXT",
          "value_handler" => \&handle_math_identifier_value,
	 },
       'number' =>
         {
	  "value_type" => "TEXT",
          "value_handler" => \&handle_math_number_value,
	 },
       'operator' =>
         {
	  "value_type" => "TEXT",
          "value_handler" => \&handle_math_operator_value,
	 },
       'separator' =>
         {
	  "value_type" => "TEXT",
          "value_handler" => \&handle_math_separator_value,
	 },
       'hspace' =>
         {
	  "value_type" => "TEXT",
          "value_handler" => \&handle_math_hspace_value,
	 },
       'block' =>
         {
	  "value_type" => "NODE_LIST",
          "value_handler" => \&handle_math_block_value,
	 },
       'fraction' =>
         {
	  "value_type" => "NODE_LIST",
	  "value_handler" => \&handle_math_fraction_value,
	 },
       'accented' =>
         {
	  "value_type" => "NODE_LIST",
	  "value_handler" => \&handle_math_accented_value,
	 },
       'overset' =>
         {
	  "value_type" => "NODE_LIST",
	  "value_handler" => \&handle_math_overset_value,
	 },
       'underset' =>
         {
	  "value_type" => "NODE_LIST",
	  "value_handler" => \&handle_math_underset_value,
	 },
       'root' =>
         {
	  "value_type" => "NODE_LIST",
	  "value_handler" => \&handle_math_root_value,
	 },
       'norm' =>
         {
	  "value_type" => "NODE",
          "value_handler" => \&handle_math_norm_value,
	 },
       'idmap' =>
         {
	  "value_type" => "NODE",
          "value_handler" => \&handle_math_idmap_value,
	 },
       'inner_product' =>
         {
	  "value_type" => "NODE_LIST",
          "value_handler" => \&handle_math_inner_product_value,
	 },
       'cnum' =>
         {
	  "value_type" => "NODE_LIST",
          "value_handler" => \&handle_math_cnum_value,
	 },
       'binom_coeff' =>
         {
	  "value_type" => "NODE_LIST",
	  "value_handler" => \&handle_math_binom_coeff_value,
	 },
       'normal_text' =>
         {
	  "value_type" => "OUTPUT_LIST",
          "value_handler" => \&handle_math_normal_text_value,
	 },
       'mtable' =>
         {
	  'value_type' =>  'NODE_LIST',
	  'value_handler' => \&handle_math_table_value,
	 },
       'table_row' =>
         {
	  'value_type' =>  'NODE_LIST',
	  'value_handler' => \&handle_math_table_row_value,
	 },
       'table_cell' =>
         {
	  'value_type' =>  'NODE_LIST',
	  'value_handler' => \&handle_math_table_cell_value,
	 },
      };
  }

# --------------------------------------------------------------------------------
# h2: Initializing
# --------------------------------------------------------------------------------

$lib_table->{math}->{initializer} = sub
  {
    # check_registered_math_nodes();

    # Initializing global namespace variables:
    $mathml_namespace = 'http://www.w3.org/1998/Math/MathML';
    $mathml_namespace_prefix = 'math';
    $mathml_ext_namespace = 'http://www.mumie.net/xml-namespace/mathml-ext';
    $mathml_namespace_prefix = 'math-ext';
    $set_mathml_namespace = FALSE;
    $set_mathml_namespace_prefix = FALSE;

    # Initializing math nodes:
    init_default_math_nodes();

    # Initializing math tokens:
    init_math_tokens();

    # Initializing default math attributes:
    init_default_math_attribs();

    # Initializing parenthesis mode:
    $mathml_paren_mode = 'PLAIN';

    # Initializing 'mod' command spaces:
    $math_mod_space_before = '1em';
    $math_mod_space_before_small = '0.5em';
    $math_mod_space_after = '0.5em';

    # Initializing command and environment tables:
    my $math_cmd_table = create_math_cmd_table();
    my $math_env_table = create_math_env_table();

    my @math_toplevel_cmds = ('[');
    my @math_cmds = keys(%{$math_cmd_table});
    rm_string_from_array(\@math_toplevel_cmds, \@math_cmds);
    my @math_toplevel_envs = ('displaymath', 'equation', 'equation*');
    my @math_envs = keys(%{$math_env_table});
    rm_string_from_array(\@math_toplevel_envs, \@math_envs);

    deploy_lib
      (
       'math',
       $math_cmd_table,
       $math_env_table,
       {
        'MATH' => \@math_cmds,
        'MATH_TOPLEVEL' => \@math_toplevel_cmds,
        'TOPLEVEL' => \@math_toplevel_cmds
       },
       {
        'MATH' => \@math_envs,
        'MATH_TOPLEVEL' => \@math_toplevel_envs,
        'TOPLEVEL' => \@math_toplevel_envs
       }
      );

    require_lib("counter");
    new_counter("equation");

    require_lib("length");

    require_lib("math_table");
    require_lib("math_matrix");
    require_lib("math_align");
    require_lib("math_commdiag");
  };

return(TRUE);
