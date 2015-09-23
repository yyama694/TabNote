package org.yyama.tabnote.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

public class LineEditTextView extends EditText {
	public boolean underLine;
	public int fontSize;

	public LineEditTextView(Context context) {
		super(context);
	}

	public LineEditTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (underLine) {
			// EditText�r���[�̃��C�A�E�g��̕�
			int width = getMeasuredWidth();
			// EditText�r���[�̃��C�A�E�g��̍����B�p�f�B���O���l������K�v������B
			int height = getMeasuredHeight() - getExtendedPaddingTop()
					- getExtendedPaddingBottom();
			// �p�f�B���O���l��
			int paddingTop = getExtendedPaddingTop();
			// �e�L�X�g�̍���
			int lineHeight = getLineHeight();
			// �L���ȕ`��̈悩��s�����v�Z
			int textCount = height / lineHeight;
			// ���͂��ꂽ�s���B��ʂ̍������傫���Ȃ邱�Ƃ�����
			int lines = this.getLineCount();
			// �L���ȍs���Ǝ��ۂɓ��͂��ꂽ�s���̂����A�傫����
			int lineCount = Math.max(textCount, lines);

			float[] points = new float[(lineCount << 2) + 4];

			// �����̒�������C�ɕ`�悷��̂ō��W�ʒu���v�Z
			for (int i = 1; i <= lineCount; i++) {
				points[(i << 2) + 0] = 0;
				points[(i << 2) + 1] = i * lineHeight + paddingTop;
				points[(i << 2) + 2] = width;
				points[(i << 2) + 3] = i * lineHeight + paddingTop;
			}

			// �r���F�̐ݒ�
			Paint paint = getPaint();
			paint.setColor(0x55000000);

			// �����̕`��
			canvas.drawLines(points, paint);
		}
		setTextSize(fontSize);
		// �e�N���X�̕`�揈��
		super.onDraw(canvas);
	}
}
