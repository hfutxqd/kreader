package com.neverland.engbook.unicode;

import com.neverland.engbook.util.AlStyles;

public class CP949 {
	public static char getChar(char s1, char s2) {
		char wc = 0x00;
			
		switch (s1) {
		case 0x81: wc = CP949Data80.data_81_41_FE[s2 - 0x41]; break;
		case 0x82: wc = CP949Data80.data_82_41_FE[s2 - 0x41]; break;	
		case 0x83: wc = CP949Data80.data_83_41_FE[s2 - 0x41]; break;
		case 0x84: wc = CP949Data80.data_84_41_FE[s2 - 0x41]; break;	
		case 0x85: wc = CP949Data80.data_85_41_FE[s2 - 0x41]; break;
		case 0x86: wc = CP949Data80.data_86_41_FE[s2 - 0x41]; break;	
		case 0x87: wc = CP949Data80.data_87_41_FE[s2 - 0x41]; break;
		case 0x88: wc = CP949Data80.data_88_41_FE[s2 - 0x41]; break;	
		case 0x89: wc = CP949Data80.data_89_41_FE[s2 - 0x41]; break;
		case 0x8a: wc = CP949Data80.data_8A_41_FE[s2 - 0x41]; break;	
		case 0x8b: wc = CP949Data80.data_8B_41_FE[s2 - 0x41]; break;	
		case 0x8c: wc = CP949Data80.data_8C_41_FE[s2 - 0x41]; break;
		case 0x8d: wc = CP949Data80.data_8D_41_FE[s2 - 0x41]; break;	
		case 0x8e: wc = CP949Data80.data_8E_41_FE[s2 - 0x41]; break;
		case 0x8f: wc = CP949Data80.data_8F_41_FE[s2 - 0x41]; break;
		case 0x90: wc = CP949Data80.data_90_41_FE[s2 - 0x41]; break;
		case 0x91: wc = CP949Data80.data_91_41_FE[s2 - 0x41]; break;
		case 0x92: wc = CP949Data80.data_92_41_FE[s2 - 0x41]; break;	
		case 0x93: wc = CP949Data80.data_93_41_FE[s2 - 0x41]; break;
		case 0x94: wc = CP949Data80.data_94_41_FE[s2 - 0x41]; break;	
		case 0x95: wc = CP949Data80.data_95_41_FE[s2 - 0x41]; break;
		case 0x96: wc = CP949Data80.data_96_41_FE[s2 - 0x41]; break;	
		case 0x97: wc = CP949Data80.data_97_41_FE[s2 - 0x41]; break;
		case 0x98: wc = CP949Data80.data_98_41_FE[s2 - 0x41]; break;	
		case 0x99: wc = CP949Data80.data_99_41_FE[s2 - 0x41]; break;
		case 0x9a: wc = CP949Data80.data_9A_41_FE[s2 - 0x41]; break;	
		case 0x9b: wc = CP949Data80.data_9B_41_FE[s2 - 0x41]; break;	
		case 0x9c: wc = CP949Data80.data_9C_41_FE[s2 - 0x41]; break;
		case 0x9d: wc = CP949Data80.data_9D_41_FE[s2 - 0x41]; break;	
		case 0x9e: wc = CP949Data80.data_9E_41_FE[s2 - 0x41]; break;
		case 0x9f: wc = CP949Data80.data_9F_41_FE[s2 - 0x41]; break;
		case 0xa0: wc = CP949DataA0.data_A0_41_FE[s2 - 0x41]; break;
		case 0xa1: wc = CP949DataA0.data_A1_41_FE[s2 - 0x41]; break;
		case 0xa2: if (s2 >= 0x41 && s2 <= 0xe7) wc = CP949DataA0.data_A2_41_E7[s2 - 0x41]; break;	
		case 0xa3: wc = CP949DataA0.data_A3_41_FE[s2 - 0x41]; break;
		case 0xa4: wc = CP949DataA0.data_A4_41_FE[s2 - 0x41]; break;	
		case 0xa5: if (s2 >= 0x41 && s2 <= 0xf8) wc = CP949DataA0.data_A5_41_F8[s2 - 0x41]; break;
		case 0xa6: if (s2 >= 0x41 && s2 <= 0xe4) wc = CP949DataA0.data_A6_41_E4[s2 - 0x41]; break;	
		case 0xa7: if (s2 >= 0x41 && s2 <= 0xef) wc = CP949DataA0.data_A7_41_EF[s2 - 0x41]; break;
		case 0xa8: wc = CP949DataA0.data_A8_41_FE[s2 - 0x41]; break;	
		case 0xa9: wc = CP949DataA0.data_A9_41_FE[s2 - 0x41]; break;
		case 0xaa: if (s2 >= 0x41 && s2 <= 0xf3) wc = CP949DataA0.data_AA_41_F3[s2 - 0x41]; break;	
		case 0xab: if (s2 >= 0x41 && s2 <= 0xf6) wc = CP949DataA0.data_AB_41_F6[s2 - 0x41]; break;	
		case 0xac: if (s2 >= 0x41 && s2 <= 0xf1) wc = CP949DataA0.data_AC_41_F1[s2 - 0x41]; break;
		case 0xad: if (s2 >= 0x41 && s2 <= 0xa0) wc = CP949DataA0.data_AD_41_A0[s2 - 0x41]; break;	
		case 0xae: if (s2 >= 0x41 && s2 <= 0xa0) wc = CP949DataA0.data_AE_41_A0[s2 - 0x41]; break;
		case 0xaf: if (s2 >= 0x41 && s2 <= 0xa0) wc = CP949DataA0.data_AF_41_A0[s2 - 0x41]; break;
		case 0xb0: wc = CP949DataA0.data_B0_41_FE[s2 - 0x41]; break;
		case 0xb1: wc = CP949DataA0.data_B1_41_FE[s2 - 0x41]; break;
		case 0xb2: wc = CP949DataA0.data_B2_41_FE[s2 - 0x41]; break;	
		case 0xb3: wc = CP949DataA0.data_B3_41_FE[s2 - 0x41]; break;
		case 0xb4: wc = CP949DataA0.data_B4_41_FE[s2 - 0x41]; break;	
		case 0xb5: wc = CP949DataA0.data_B5_41_FE[s2 - 0x41]; break;
		case 0xb6: wc = CP949DataA0.data_B6_41_FE[s2 - 0x41]; break;	
		case 0xb7: wc = CP949DataA0.data_B7_41_FE[s2 - 0x41]; break;
		case 0xb8: wc = CP949DataA0.data_B8_41_FE[s2 - 0x41]; break;	
		case 0xb9: wc = CP949DataA0.data_B9_41_FE[s2 - 0x41]; break;
		case 0xba: wc = CP949DataA0.data_BA_41_FE[s2 - 0x41]; break;	
		case 0xbb: wc = CP949DataA0.data_BB_41_FE[s2 - 0x41]; break;	
		case 0xbc: wc = CP949DataA0.data_BC_41_FE[s2 - 0x41]; break;
		case 0xbd: wc = CP949DataA0.data_BD_41_FE[s2 - 0x41]; break;	
		case 0xbe: wc = CP949DataA0.data_BE_41_FE[s2 - 0x41]; break;
		case 0xbf: wc = CP949DataA0.data_BF_41_FE[s2 - 0x41]; break;
		case 0xc0: wc = CP949DataC0.data_C0_41_FE[s2 - 0x41]; break;
		case 0xc1: wc = CP949DataC0.data_C1_41_FE[s2 - 0x41]; break;
		case 0xc2: wc = CP949DataC0.data_C2_41_FE[s2 - 0x41]; break;	
		case 0xc3: wc = CP949DataC0.data_C3_41_FE[s2 - 0x41]; break;
		case 0xc4: wc = CP949DataC0.data_C4_41_FE[s2 - 0x41]; break;	
		case 0xc5: wc = CP949DataC0.data_C5_41_FE[s2 - 0x41]; break;
		case 0xc6: wc = CP949DataC0.data_C6_41_FE[s2 - 0x41]; break;	
		case 0xc7: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_C7_A1_FE[s2 - 0xa1]; break;
		case 0xc8: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_C8_A1_FE[s2 - 0xa1]; break;	
		//case 0xc9: wc = CP949DataC0.data_C9_41_FE[s2 - 0x41]; break;
		case 0xca: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_CA_A1_FE[s2 - 0xa1]; break;	
		case 0xcb: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_CB_A1_FE[s2 - 0xa1]; break;	
		case 0xcc: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_CC_A1_FE[s2 - 0xa1]; break;
		case 0xcd: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_CD_A1_FE[s2 - 0xa1]; break;	
		case 0xce: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_CE_A1_FE[s2 - 0xa1]; break;
		case 0xcf: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_CF_A1_FE[s2 - 0xa1]; break;
		case 0xd0: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_D0_A1_FE[s2 - 0xa1]; break;
		case 0xd1: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_D1_A1_FE[s2 - 0xa1]; break;
		case 0xd2: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_D2_A1_FE[s2 - 0xa1]; break;	
		case 0xd3: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_D3_A1_FE[s2 - 0xa1]; break;
		case 0xd4: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_D4_A1_FE[s2 - 0xa1]; break;	
		case 0xd5: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_D5_A1_FE[s2 - 0xa1]; break;
		case 0xd6: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_D6_A1_FE[s2 - 0xa1]; break;	
		case 0xd7: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_D7_A1_FE[s2 - 0xa1]; break;
		case 0xd8: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_D8_A1_FE[s2 - 0xa1]; break;	
		case 0xd9: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_D9_A1_FE[s2 - 0xa1]; break;
		case 0xda: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_DA_A1_FE[s2 - 0xa1]; break;	
		case 0xdb: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_DB_A1_FE[s2 - 0xa1]; break;	
		case 0xdc: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_DC_A1_FE[s2 - 0xa1]; break;
		case 0xdd: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_DD_A1_FE[s2 - 0xa1]; break;	
		case 0xde: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_DE_A1_FE[s2 - 0xa1]; break;
		case 0xdf: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataC0.data_DF_A1_FE[s2 - 0xa1]; break;
		case 0xe0: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_E0_A1_FE[s2 - 0xa1]; break;
		case 0xe1: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_E1_A1_FE[s2 - 0xa1]; break;
		case 0xe2: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_E2_A1_FE[s2 - 0xa1]; break;	
		case 0xe3: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_E3_A1_FE[s2 - 0xa1]; break;
		case 0xe4: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_E4_A1_FE[s2 - 0xa1]; break;	
		case 0xe5: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_E5_A1_FE[s2 - 0xa1]; break;
		case 0xe6: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_E6_A1_FE[s2 - 0xa1]; break;	
		case 0xe7: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_E7_A1_FE[s2 - 0xa1]; break;
		case 0xe8: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_E8_A1_FE[s2 - 0xa1]; break;	
		case 0xe9: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_E9_A1_FE[s2 - 0xa1]; break;
		case 0xea: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_EA_A1_FE[s2 - 0xa1]; break;	
		case 0xeb: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_EB_A1_FE[s2 - 0xa1]; break;	
		case 0xec: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_EC_A1_FE[s2 - 0xa1]; break;
		case 0xed: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_ED_A1_FE[s2 - 0xa1]; break;	
		case 0xee: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_EE_A1_FE[s2 - 0xa1]; break;
		case 0xef: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_EF_A1_FE[s2 - 0xa1]; break;
		case 0xf0: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_F0_A1_FE[s2 - 0xa1]; break;
		case 0xf1: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_F1_A1_FE[s2 - 0xa1]; break;
		case 0xf2: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_F2_A1_FE[s2 - 0xa1]; break;	
		case 0xf3: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_F3_A1_FE[s2 - 0xa1]; break;
		case 0xf4: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_F4_A1_FE[s2 - 0xa1]; break;	
		case 0xf5: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_F5_A1_FE[s2 - 0xa1]; break;
		case 0xf6: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_F6_A1_FE[s2 - 0xa1]; break;	
		case 0xf7: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_F7_A1_FE[s2 - 0xa1]; break;
		case 0xf8: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_F8_A1_FE[s2 - 0xa1]; break;	
		case 0xf9: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_F9_A1_FE[s2 - 0xa1]; break;
		case 0xfa: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_FA_A1_FE[s2 - 0xa1]; break;	
		case 0xfb: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_FB_A1_FE[s2 - 0xa1]; break;	
		case 0xfc: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_FC_A1_FE[s2 - 0xa1]; break;
		case 0xfd: if (s2 >= 0xa1 && s2 <= 0xfe) wc = CP949DataE0.data_FD_A1_FE[s2 - 0xa1]; break;	
		//case 0xfe: if (s2 >= 0x41 && s2 <= 0xfe) wc = CP949DataE0.data_FE_41_FE[s2 - 0x41]; break;
		//case 0xff: if (s2 >= 0x41 && s2 <= 0xfe) wc = CP949DataE0.data_FF_41_FE[s2 - 0x41]; break;
		}
		
		if ((wc & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0)
			wc = 0x00;
		
		return wc;
	}

}
