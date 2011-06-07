<xsl:transform
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 version="1.1"
>

<!--
    XSLT stylesheet to perform a knight's tour of the chessboard.
    Author: Michael H. Kay
    Date: 11 December 2000
    
    ** This version of the stylesheet was written to work with the abandoned
    ** XSLT 1.1 working draft: it uses temporary trees as working data, rather than strings.
    ** This version was published in the second edition of XSLT Programmer's Reference

    This stylesheet can be run using any XML file as a dummy source document.
    There is an optional parameter, start, which can be set to any square on the
    chessboard, e.g. a3 or h5. By default the tour starts at a1.

    The output is an HTML display of the completed tour.

    Internally, the following data representations are used:
    * A square on the chessboard: represented as a number in the range 0 to 63
    * A state of the chessboard: a tree containing 64 <square> elements, each
      <square> containing a move number (values 00-63) followed by a colon. A square that has
      not been visited yet is represented by an empty element.
    * A set of possible moves: represented as a tree containing a list of <move> elements,
      where each <move> contains an integer representing the number of the destination square
      
-->

<xsl:param name="start" select="'a1'"/>

<!-- start-column is an integer in the range 0-7 -->

<xsl:variable name="start-column"
    select="number(translate(substring($start, 1, 1),
            'abcdefgh', '01234567'))"/>

<!-- start-row is an integer in the range 0-7, with zero at the top -->

<xsl:variable name="start-row"
    select="8 - number(substring($start, 2, 1))"/>

<xsl:template match="/">

    <!-- This template controls the processing. It does not access the source document. -->

    <!-- Validate the input parameter -->

    <xsl:if test="not(string-length($start)=2) or
        not(translate(substring($start,1,1), 'abcdefgh', 'aaaaaaaa')='a') or
        not(translate(substring($start,2,1), '12345678', '11111111')='1')">
        <xsl:message terminate="yes">Invalid start parameter: try say 'a1' or 'g6'</xsl:message>
    </xsl:if>

    <!-- Set up the empty board -->

    <xsl:variable name="empty-board">
        <xsl:call-template name="make-board"/>
    </xsl:variable>

    <!-- Place the knight on the board at the chosen starting position -->
    
    <xsl:variable name="initial-board">
        <xsl:call-template name="place-knight">
            <xsl:with-param name="move" select="1"/>
            <xsl:with-param name="board" select="$empty-board"/>
            <xsl:with-param name="square" select="$start-row * 8 + $start-column"/>
        </xsl:call-template>
    </xsl:variable>

    <!-- Evaluate the knight's tour -->

    <xsl:variable name="final-board">
        <xsl:call-template name="make-moves">
            <xsl:with-param name="move" select="2"/>
            <xsl:with-param name="board" select="$initial-board"/>
            <xsl:with-param name="square" select="$start-row * 8 + $start-column"/>
        </xsl:call-template>
    </xsl:variable>

    <!-- produce the HTML output -->

    <xsl:call-template name="print-board">
        <xsl:with-param name="board" select="$final-board"/>
    </xsl:call-template>

</xsl:template>

<xsl:template name="make-board">

    <!-- This template creates the initial empty board. It writes out one square and then
         calls itself to write the remaining squares -->

    <xsl:param name="size" select="64"/>
    <xsl:if test="$size!=0">
        <square/>
        <xsl:call-template name="make-board">
            <xsl:with-param name="size" select="$size - 1"/>
        </xsl:call-template>
    </xsl:if>
</xsl:template>

<xsl:template name="place-knight">

    <!-- This template places a knight on the board at a given square. The returned value is
         the supplied board, modified to indicate that the knight reached a given square at a given
         move -->

    <xsl:param name="move"/>
    <xsl:param name="board"/>
    <xsl:param name="square"/>

    <xsl:copy-of select="$board/square[position()-1 &lt; $square]"/>
    <square><xsl:value-of select="$move"/></square>
    <xsl:copy-of select="$board/square[position()-1 &gt; $square]"/>

</xsl:template>

<xsl:template name="make-moves">

    <!-- This template takes the board in a given state, decides on the next move to make,
         and then calls itself recursively to make further moves, until the knight has completed
         his tour of the board. -->

    <xsl:param name="move"/>
    <xsl:param name="board"/>
    <xsl:param name="square"/>

    <!-- determine the possible moves that the knight can make -->

    <xsl:variable name="possible-move-list">
        <xsl:call-template name="list-possible-moves">
            <xsl:with-param name="board" select="$board"/>
            <xsl:with-param name="square" select="$square"/>
        </xsl:call-template>
    </xsl:variable>

    <!-- try these moves in turn until one is found that works -->

    <xsl:call-template name="try-possible-moves">
        <xsl:with-param name="board" select="$board"/>
        <xsl:with-param name="square" select="$square"/>
        <xsl:with-param name="move" select="$move"/>
        <xsl:with-param name="possible-moves" select="$possible-move-list/move"/>
    </xsl:call-template>

</xsl:template>

<xsl:template name="try-possible-moves">

    <!-- This template tries a sequence of possible moves that the knight can make
         from a given position. It determines the best move as the one to the square with
         fewest exits. If this is unsuccessful then in principle it can backtrack and
         try another move; however this turns out never to be necessary. -->

    <xsl:param name="move"/>
    <xsl:param name="board"/>
    <xsl:param name="square"/>
    <xsl:param name="possible-moves"/>

    <xsl:choose>
    <xsl:when test="$possible-moves">

        <!-- if at least one move is possible, find the best one -->
    
        <xsl:variable name="best-move">
            <xsl:call-template name="find-best-move">
                <xsl:with-param name="board" select="$board"/>            
                <xsl:with-param name="possible-moves" select="$possible-moves"/>
            </xsl:call-template>
        </xsl:variable>

        <!-- find the list of possible moves excluding the best one -->

        <xsl:variable name="other-possible-moves" select="$possible-moves[. != $best-move]"/>

        <!-- update the board to make the move chosen as the best one -->

        <xsl:variable name="next-board">
            <xsl:call-template name="place-knight">
                <xsl:with-param name="move" select="$move"/>
                <xsl:with-param name="board" select="$board"/>
                <xsl:with-param name="square" select="$best-move"/>
            </xsl:call-template>
        </xsl:variable>
        
        <!-- now make further moves using a recursive call, until the board is complete -->

        <xsl:variable name="final-board">
            <xsl:choose>
            <xsl:when test="$next-board/square = ''">   <!-- if there is an empty square -->
                <xsl:call-template name="make-moves">
                    <xsl:with-param name="move" select="$move + 1"/>
                    <xsl:with-param name="board" select="$next-board"/>
                    <xsl:with-param name="square" select="$best-move"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="$next-board"/>
            </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <!-- if the final board has the special value '##', we got stuck, and have to choose
             the next best of the possible moves. This is done by a recursive call. In practice,
             we never do get stuck, so this path is not taken. -->

        <xsl:choose>
        <xsl:when test="$final-board='##'">
            <xsl:message>Backtracking at move <xsl:value-of select="$move"/></xsl:message>
            <xsl:call-template name="try-possible-moves">
                <xsl:with-param name="board" select="$board"/>
                <xsl:with-param name="square" select="$square"/>
                <xsl:with-param name="move" select="$move"/>
                <xsl:with-param name="possible-moves" select="$other-possible-moves"/>
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
            <xsl:copy-of select="$final-board"/>
        </xsl:otherwise>
        </xsl:choose>
    </xsl:when>
    
    <xsl:otherwise>

        <!-- if there is no possible move, we return the special value '##' as the final state
             of the board, to indicate to the caller that we got stuck -->
    
        <xsl:value-of select="'##'"/>
    </xsl:otherwise>
    </xsl:choose>
    
</xsl:template>



<xsl:template name="find-best-move">

    <!-- This template finds from among the possible moves, the one with fewest exits.
         It calls itself recursively. -->
         
    <xsl:param name="board"/>            
    <xsl:param name="possible-moves"/>
    <xsl:param name="fewest-exits" select="9"/>
    <xsl:param name="best-so-far" select="'XX'"/>

    <!-- split the list of possible moves into the first move and the rest of the moves -->

    <xsl:variable name="trial-move" select="number($possible-moves[1])"/>
    <xsl:variable name="other-possible-moves" select="$possible-moves[position() &gt; 1]"/>

    <!-- try making the first move -->

    <xsl:variable name="trial-board">
        <xsl:call-template name="place-knight">
            <xsl:with-param name="board" select="$board"/>
            <xsl:with-param name="move" select="99"/>
            <xsl:with-param name="square" select="$trial-move"/>
        </xsl:call-template>
    </xsl:variable>

    <!-- see how many moves would be possible the next time -->

    <xsl:variable name="trial-move-exit-list">
        <xsl:call-template name="list-possible-moves">
            <xsl:with-param name="board" select="$trial-board"/>
            <xsl:with-param name="square" select="$trial-move"/>
        </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="number-of-exits" select="count($trial-move-exit-list/move)"/>

    <!-- determine whether this trial move has fewer exits than those considered up till now -->

    <xsl:variable name="minimum-exits">
        <xsl:choose>
        <xsl:when test="$number-of-exits &lt; $fewest-exits">
            <xsl:value-of select="$number-of-exits"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="$fewest-exits"/>
        </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <!-- determine which is the best move (the one with fewest exits) so far -->

    <xsl:variable name="new-best-so-far">    
        <xsl:choose>
        <xsl:when test="$number-of-exits &lt; $fewest-exits">
            <xsl:value-of select="$trial-move"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="$best-so-far"/>
        </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <!-- if there are other possible moves, consider them too, using a recursive call.
         Otherwise return the best move found. -->

    <xsl:choose>
    <xsl:when test="$other-possible-moves">
        <xsl:call-template name="find-best-move">
            <xsl:with-param name="board" select="$board"/>
            <xsl:with-param name="possible-moves" select="$other-possible-moves"/>
            <xsl:with-param name="fewest-exits" select="$minimum-exits"/>
            <xsl:with-param name="best-so-far" select="$new-best-so-far"/>
        </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
        <xsl:value-of select="$new-best-so-far"/>
    </xsl:otherwise>
    </xsl:choose>
</xsl:template>


<xsl:template name="list-possible-moves">

    <!-- This template, given the knight's position on the board, returns the set of squares
         he can move to, as list of <move> elements. The squares will
         be ones that have not been visited before -->
         
    <xsl:param name="board"/>
    <xsl:param name="square"/>
    <xsl:variable name="row" select="$square div 8"/>
    <xsl:variable name="column" select="$square mod 8"/>

    <xsl:if test="$row &gt; 1 and $column &gt; 0 and $board/square[($square - 17) + 1]=''">
        <move><xsl:value-of select="$square - 17"/></move>
    </xsl:if>
    <xsl:if test="$row &gt; 1 and $column &lt; 7 and $board/square[($square - 15) + 1]=''">
        <move><xsl:value-of select="$square - 15"/></move>
    </xsl:if>
    <xsl:if test="$row &gt; 0 and $column &gt; 1 and $board/square[($square - 10) + 1]=''">
        <move><xsl:value-of select="$square - 10"/></move>
    </xsl:if>
    <xsl:if test="$row &gt; 0 and $column &lt; 6 and $board/square[($square - 6) + 1]=''">
        <move><xsl:value-of select="$square - 6"/></move>
    </xsl:if>
    <xsl:if test="$row &lt; 6 and $column &gt; 0 and $board/square[($square + 15) + 1]=''">
        <move><xsl:value-of select="$square + 15"/></move>
    </xsl:if>
    <xsl:if test="$row &lt; 6 and $column &lt; 7 and $board/square[($square + 17) + 1]=''">
        <move><xsl:value-of select="$square + 17"/></move>
    </xsl:if>
    <xsl:if test="$row &lt; 7 and $column &gt; 1 and $board/square[($square + 6) + 1]=''">
        <move><xsl:value-of select="$square + 6"/></move>
    </xsl:if>
    <xsl:if test="$row &lt; 7 and $column &lt; 6 and $board/square[($square + 10) + 1]=''">
        <move><xsl:value-of select="$square + 10"/></move>
    </xsl:if>
</xsl:template>

<xsl:template name="print-board">

    <!-- Output the board in HTML format -->

    <xsl:param name="board"/>

    <html>
    <head>
        <title>Knight's tour</title>
    </head>
    <body>
    <div align="center">
    <h1>Knight's tour starting at <xsl:value-of select="$start"/></h1>
    <table border="1" cellpadding="4">
        <xsl:call-template name="print-rows">
            <xsl:with-param name="board" select="$board"/>
        </xsl:call-template>
    </table>
    </div>
    </body>
    </html>
</xsl:template>

<xsl:template name="print-rows">

    <!-- This template prints the rows of the board. It actually prints the first row
         and calls itself to process the remainder -->
         
    <xsl:param name="board"/>
    <xsl:param name="row" select="0"/>
    <xsl:if test="$row &lt; 8">
        <tr>
        <xsl:call-template name="print-columns">
            <xsl:with-param name="board" select="$board"/>
            <xsl:with-param name="row" select="$row"/>
        </xsl:call-template>
        </tr>

        <xsl:call-template name="print-rows">
            <xsl:with-param name="board" select="$board"/>
            <xsl:with-param name="row" select="$row + 1"/>
        </xsl:call-template>
    </xsl:if>
</xsl:template>

<xsl:template name="print-columns">

    <!-- This template prints the columns of a row. It actually prints the first column
         and calls itself to process the remainder -->

    <xsl:param name="board"/>
    <xsl:param name="row"/>
    <xsl:param name="column" select="0"/>
    <xsl:if test="$column &lt; 8">
        <xsl:variable name="color">
            <xsl:choose>
            <xsl:when test="($row + $column) mod 2">xffff44</xsl:when>
            <xsl:otherwise>white</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <td align="center" bgcolor="{$color}">
            <xsl:value-of select="$board/square[$row*8 + $column + 1]"/>
        </td>

        <xsl:call-template name="print-columns">
            <xsl:with-param name="board" select="$board"/>
            <xsl:with-param name="row" select="$row"/>
            <xsl:with-param name="column" select="$column + 1"/>
        </xsl:call-template>
    </xsl:if>    
</xsl:template>

</xsl:transform>

